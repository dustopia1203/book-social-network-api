package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.request.ChangePasswordRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.BookService;
import com.dustopia.book_social_network_api.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService userService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            Principal connectedUser
    ) throws IllegalAccessException {
        userService.changePassword(changePasswordRequest, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
