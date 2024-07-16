package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.request.ChangePasswordRequest;
import com.dustopia.book_social_network_api.repository.UserRepository;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest, Principal connectedUser) throws IllegalAccessException {
        User user = ((CustomUserDetails) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal()).getUser();
        if (user == null) {
            throw new UsernameNotFoundException("User not found!");
        }
        if (!passwordEncoder.matches(changePasswordRequest.oldPassword(), user.getPassword())) {
            throw new IllegalAccessException("Wrong password");
        }
        if (!changePasswordRequest.newPassword().equals(changePasswordRequest.confirmNewPassword())) {
            throw new IllegalAccessException("Password are not the same");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.newPassword()));
        userRepository.save(user);
    }

}
