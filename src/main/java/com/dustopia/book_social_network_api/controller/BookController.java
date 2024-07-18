package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import com.dustopia.book_social_network_api.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllDisplayableBooks(
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "10") int size,
            Authentication connectedUser
    ) {
        PageData<BookDto> books = bookService.findAllDisplayableBooks(page, size, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", books));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateBook(
            @PathVariable Long id,
            @RequestBody BookRequest bookRequest,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.updateBookById(id, bookRequest, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteBook(
            @PathVariable Long id,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.deleteBookById(id, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

    @PostMapping("/{id}/upload-cover")
    public ResponseEntity<ResponseObject> uploadBookCover(
            @PathVariable Long id,
            @RequestParam("file")MultipartFile file,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.uploadBookCover(id, file, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

}
