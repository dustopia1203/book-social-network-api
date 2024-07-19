package com.dustopia.book_social_network_api.model.mapper;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.request.BookRequest;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toBook(BookRequest bookRequest) {
        return Book
                .builder()
                .title(bookRequest.title())
                .author(bookRequest.author())
                .synopsis(bookRequest.synopsis())
                .isShareable(bookRequest.isShareable())
                .url(null)
                .build();
    }

    public BookDto toBookDto(Book book) {
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSynopsis(),
                book.getBookCoverUrl(),
                book.getUrl(),
                book.getUser().getFullName(),
                book.getRate(),
                book.isShareable()
        );
    }

    public BookDto toBookDto(BookTransaction bookTransaction) {
        Book book = bookTransaction.getBook();
        return new BookDto(
                book.getId(),
                book.getTitle(),
                book.getAuthor(),
                book.getSynopsis(),
                book.getBookCoverUrl(),
                book.getUrl(),
                book.getUser().getFullName(),
                book.getRate(),
                book.isShareable()
        );
    }

}
