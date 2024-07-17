package com.dustopia.book_social_network_api.model.mapper;

import com.dustopia.book_social_network_api.model.dto.BookDto;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.repository.BookRequest;
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
                .isArchived(false)
                .build();
    }

    public BookDto toBookDto(Book book) {
        return new BookDto(
                book.getTitle(),
                book.getAuthor(),
                book.getSynopsis(),
                book.getBookCover(),
                book.getUser().getFullName(),
                book.getRate()
        );
    }

}
