package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public interface BookService {

    BookDto addBook(BookRequest bookRequest, Authentication connectedUser);

    BookDto getBookById(Long id, Authentication connectedUser);

    PageData<BookDto> findAllDisplayableBooks(int page, int size, Authentication connectedUser);

    PageData<BookDto> findAllBooksByOwner(int page, int size, Authentication connectedUser);

    PageData<BookDto> findAllPurchasedBooks(int page, int size, Authentication connectedUser);

    BookDto updateBookById(Long id, BookRequest bookRequest, Authentication connectedUser);

    BookDto deleteBookById(Long id, Authentication connectedUser);

    BookDto uploadBookCover(Long id, MultipartFile file, Authentication connectedUser);

    BookDto uploadBook(Long id, MultipartFile file, Authentication connectedUser);
}
