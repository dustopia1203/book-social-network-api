package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.model.request.LoginRequest;
import com.dustopia.book_social_network_api.model.dto.UserDto;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.UserMapper;
import com.dustopia.book_social_network_api.model.request.RegisterRequest;
import com.dustopia.book_social_network_api.model.response.AuthenticationResponse;
import com.dustopia.book_social_network_api.repository.UserRepository;
import com.dustopia.book_social_network_api.security.jwt.JwtService;
import com.dustopia.book_social_network_api.service.AuthenticationService;
import com.dustopia.book_social_network_api.service.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;

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

    @Override
    public AuthenticationResponse loginAndAuthenticate(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password()));
        if (authentication.isAuthenticated()) {
            String jwt = jwtService.generateToken(userDetailsService.loadUserByUsername(loginRequest.email()));
            String refreshToken = jwtService.generateRefreshToken(userDetailsService.loadUserByUsername(loginRequest.email()));
            tokenService.revokeAllTokensOfUser(loginRequest.email());
            tokenService.saveToken(loginRequest.email(), jwt);
            return new AuthenticationResponse(jwt, refreshToken);
        } else {
            throw new UsernameNotFoundException("Invalid credentials");
        }
    }

    @Override
    public UserDto registerNewUser(RegisterRequest registerRequest) {
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

    private void sendValidationEmail(User user) {

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
                tokenService.saveToken(username, accessToken);
                AuthenticationResponse authResponse = new AuthenticationResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

}
