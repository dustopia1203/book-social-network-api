package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.exception.BookUnavailableException;
import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.BookMapper;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.repository.BookRequest;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    @Override
    public BookDto addBook(BookRequest bookRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book newBook = bookMapper.toBook(bookRequest);
        newBook.setUser(user);
        bookRepository.save(newBook);
        return bookMapper.toBookDto(newBook);
    }

    @Override
    public BookDto getBookById(Long id, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!book.isShareable()) {
            if (!user.getId().equals(book.getUser().getId()) && !user.getRole().equals("ADMIN")) {
                throw new BookUnavailableException("Book is currently unavailable");
            }
        }
        return bookMapper.toBookDto(book);
    }

}
