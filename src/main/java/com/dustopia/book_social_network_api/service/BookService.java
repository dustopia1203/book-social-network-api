package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.repository.BookRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface BookService {

    Book addBook(BookRequest bookRequest, Principal connectedUser);

}
