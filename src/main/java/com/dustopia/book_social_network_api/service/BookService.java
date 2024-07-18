package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    BookDto addBook(BookRequest bookRequest, Authentication connectedUser);

    BookDto getBookById(Long id, Authentication connectedUser);

    PageData<BookDto> findAllDisplayableBooks(int page, int size, Authentication connectedUser);

    PageData<BookDto> findAllBooksByOwner(int page, int size, Authentication connectedUser);
}
