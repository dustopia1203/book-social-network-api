package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.repository.BookRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public interface BookService {

    BookDto addBook(BookRequest bookRequest, Authentication connectedUser);

    BookDto getBookById(Long id, Authentication connectedUser);

}
