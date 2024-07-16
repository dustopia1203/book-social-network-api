package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.model.entity.Token;
import com.dustopia.book_social_network_api.model.request.LoginRequest;
import com.dustopia.book_social_network_api.model.dto.UserDto;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.UserMapper;
import com.dustopia.book_social_network_api.model.request.RegisterRequest;
import com.dustopia.book_social_network_api.model.response.AuthenticationData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.repository.TokenRepository;
import com.dustopia.book_social_network_api.repository.UserRepository;
import com.dustopia.book_social_network_api.security.jwt.JwtService;
import com.dustopia.book_social_network_api.service.AuthenticationService;
import com.dustopia.book_social_network_api.service.EmailService;
import com.dustopia.book_social_network_api.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    private final TokenService tokenService;

    private final TokenRepository tokenRepository;

    private final EmailService emailService;

    @Value("${app.mailing.frontend.activation-url}")
    private String activateUrl;

    @Override
    public AuthenticationData loginAndAuthenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        if (authentication.isAuthenticated()) {
            String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername(loginRequest.email()));
            String refreshToken = jwtService.generateRefreshToken(userDetailsService.loadUserByUsername(loginRequest.email()));
            tokenService.revokeAllTokensOfUser(loginRequest.email());
            tokenService.saveJwtToken(loginRequest.email(), jwt);
            return new AuthenticationData(jwt, refreshToken);
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @Override
    public UserDto registerNewUser(RegisterRequest registerRequest) throws MessagingException {
        if (userRepository.existsByEmail(registerRequest.email())) {
            throw new RuntimeException("Username existed");
        }
        User user = User
                .builder()
                .firstName(registerRequest.firstName())
                .lastName(registerRequest.lastName())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .accountLocked(false)
                .isEnabled(false)
                .role("USER")
                .build();
        userRepository.save(user);
        sendValidationEmail(user);
        return userMapper.toUserDto(user);
    }

    private void sendValidationEmail(User user) throws MessagingException {
        String activationToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
                user.getEmail(),
                user.getFullName(),
                "Account activation",
                "active_account",
                activateUrl,
                activationToken
        );
    }

    private String generateAndSaveActivationToken(User user) {
        String activationToken = generateActivationCode(6);
        Token token = Token
                .builder()
                .email(user.getEmail())
                .type("ACTIVATION")
                .token(activationToken)
                .isRevoked(false)
                .build();
        tokenRepository.save(token);
        return activationToken;
    }

    private String generateActivationCode(int length) {
        String characters = "0123456789";
        StringBuilder activationCode = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            activationCode.append(characters.charAt(randomIndex));
        }
        return activationCode.toString();
    }

    @Override
    public void refreshJwtToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }
        String refreshToken = authHeader.substring(7);
        String username = jwtService.extractUsername(refreshToken);
        if (username != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (userDetails != null && jwtService.isTokenValid(refreshToken)) {
                String accessToken = jwtService.generateToken(userDetails);
                tokenService.revokeAllTokensOfUser(username);
                tokenService.saveJwtToken(username, accessToken);
                AuthenticationData authenticationData = new AuthenticationData(accessToken, refreshToken);
                ResponseObject responseObject = new ResponseObject("success", authenticationData);
                new ObjectMapper().writeValue(response.getOutputStream(), responseObject);
            }
        }
    }

    @Override
    @Transactional
    public void activeAccount(String token) throws MessagingException {
        Token activationToken = tokenRepository
                .findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        User user = userRepository
                .findByEmail(activationToken.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Invalid email"));
        if (LocalDateTime.now().isAfter(activationToken.getCreatedAt().plusHours(1))) {
            activationToken.setRevoked(true);
            tokenRepository.save(activationToken);
            sendValidationEmail(user);
            throw new RuntimeException("Activation token expired! New activation code has been sent");
        }
        activationToken.setRevoked(true);
        user.setEnabled(true);
        tokenRepository.save(activationToken);
        userRepository.save(user);
    }

}
