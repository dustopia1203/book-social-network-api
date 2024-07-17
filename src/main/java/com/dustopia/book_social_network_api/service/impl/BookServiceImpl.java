package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.BookMapper;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.repository.BookRequest;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public Book addBook(BookRequest bookRequest, Principal connectedUser) {
        User user = ((CustomUserDetails) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal()).getUser();
        Book newBook = bookMapper.toBook(bookRequest);
        newBook.setUser(user);
        bookRepository.save(newBook);
        return newBook;
    }

}
