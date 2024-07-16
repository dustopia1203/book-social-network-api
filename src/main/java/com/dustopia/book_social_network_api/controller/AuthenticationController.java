package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.request.LoginRequest;
import com.dustopia.book_social_network_api.model.dto.UserDto;
import com.dustopia.book_social_network_api.model.request.RegisterRequest;
import com.dustopia.book_social_network_api.model.response.AuthenticationData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.AuthenticationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<ResponseObject> loginAndAuthenticate(
            @RequestBody @Valid LoginRequest loginRequest
    ) {
        AuthenticationData authenticationData = authenticationService.loginAndAuthenticate(loginRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", authenticationData));
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseObject> registerNewUser(
            @RequestBody @Valid RegisterRequest registerRequest
            ) throws MessagingException {
        UserDto userDto = authenticationService.registerNewUser(registerRequest);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseObject("success", userDto));
    }

    @GetMapping("/activate-account")
    public void activeAccount(
            @RequestParam String token
    ) throws MessagingException {
        authenticationService.activeAccount(token);
    }

    @PostMapping("/refresh-token")
    public void refreshJwtToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshJwtToken(request, response);
    }

}
