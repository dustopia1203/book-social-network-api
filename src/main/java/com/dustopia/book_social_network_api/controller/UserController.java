package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.request.ChangePasswordRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.BookService;
import com.dustopia.book_social_network_api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final BookService bookService;

    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest,
            Principal connectedUser
    ) throws IllegalAccessException {
        userService.changePassword(changePasswordRequest, connectedUser);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @GetMapping("/books")
    public ResponseEntity<ResponseObject> getAllBooksByOwner(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Authentication connectedUser
    ) {
        PageData<BookDto> books = bookService.findAllBooksByOwner(page, size, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", books));
    }

    @GetMapping("/purchased-books")
    public ResponseEntity<ResponseObject> getAllPurchasedBooks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Authentication connectedUser
    ) {
        PageData<BookDto> books = bookService.findAllPurchasedBooks(page, size, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", books));
    }



}
