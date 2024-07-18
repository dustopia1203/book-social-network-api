package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.exception.BookUnavailableException;
import com.dustopia.book_social_network_api.exception.PermissionDeniedAccessException;
import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.model.mapper.BookMapper;
import com.dustopia.book_social_network_api.model.response.PageData;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import com.dustopia.book_social_network_api.repository.BookTransactionRepository;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.BookService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookTransactionRepository bookTransactionRepository;

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

    @Override
    public PageData<BookDto> findAllDisplayableBooks(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public PageData<BookDto> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllBooksByOwner(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public PageData<BookDto> findAllPurchasedBooks(int page, int size, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransaction> books = bookTransactionRepository.findAllPurchasedBooks(user.getId(), pageable);
        List<BookDto> bookDtos = books
                .stream()
                .map(bookMapper::toBookDto)
                .toList();
        return new PageData<>(
                bookDtos,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public BookDto updateBookById(Long id, BookRequest bookRequest, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId())) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        if (bookRequest.title() != null) book.setTitle(bookRequest.title());
        if (bookRequest.author() != null) book.setAuthor(bookRequest.author());
        if (bookRequest.synopsis() != null) book.setSynopsis(bookRequest.synopsis());
        book.setShareable(bookRequest.isShareable());
        bookRepository.save(book);
        return bookMapper.toBookDto(book);
    }

    @Override
    public BookDto deleteBookById(Long id, Authentication connectedUser) {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + id));
        if (!user.getId().equals(book.getUser().getId()) && !user.getRole().equals("ADMIN")) {
            throw new PermissionDeniedAccessException("Current user don't have permission to do this action");
        }
        bookRepository.deleteById(id);
        return bookMapper.toBookDto(book);
    }

}
