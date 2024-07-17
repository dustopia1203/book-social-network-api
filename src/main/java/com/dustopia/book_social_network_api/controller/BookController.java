package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.repository.BookRequest;
import com.dustopia.book_social_network_api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> addBook(
            @RequestBody @Valid BookRequest bookRequest,
            Principal connectedUser
    ) {
        Book newBook = bookService.addBook(bookRequest, connectedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseObject("success", newBook));
    }

}
