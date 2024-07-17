package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.repository.BookRequest;
import com.dustopia.book_social_network_api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> addBook(
            @RequestBody @Valid BookRequest bookRequest,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.addBook(bookRequest, connectedUser);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseObject("success", bookDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getBookById(
            @PathVariable Long id,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.getBookById(id, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

}
