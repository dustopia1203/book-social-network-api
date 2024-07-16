package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.request.LoginRequest;
import com.dustopia.book_social_network_api.model.dto.UserDto;
import com.dustopia.book_social_network_api.model.request.RegisterRequest;
import com.dustopia.book_social_network_api.model.response.AuthenticationData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public interface AuthenticationService {

    AuthenticationData loginAndAuthenticate(LoginRequest loginRequest);

    UserDto registerNewUser(RegisterRequest registerRequest) throws MessagingException;

    void refreshJwtToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException;

    void activeAccount(String token) throws MessagingException;
}
