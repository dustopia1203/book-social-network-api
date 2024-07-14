package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.BookService;
import com.google.api.services.drive.model.File;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/books")
@RequiredArgsConstructor
@Tag(name = "Book")
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

    @GetMapping("/owner")
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

    @GetMapping("/purchased")
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

    @PutMapping("/{id}/upload-cover")
    public ResponseEntity<ResponseObject> uploadBookCover(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.uploadBookCover(id, file, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

    @PutMapping("/{id}/upload")
    public ResponseEntity<ResponseObject> uploadBook(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            Authentication connectedUser
    ) {
        BookDto bookDto = bookService.uploadBook(id, file, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", bookDto));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ByteArrayResource> downloadBook(
            @PathVariable Long id,
            Authentication connectedUser
    ) {
        ByteArrayResource resource = bookService.downloadBook(id, connectedUser);
        File file = bookService.getFileInfo(id);
        return ResponseEntity
                .ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .body(resource);
    }

}
