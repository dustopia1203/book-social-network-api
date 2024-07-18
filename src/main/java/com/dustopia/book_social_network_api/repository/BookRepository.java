package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookRepository extends JpaRepository<Book, Long> {

    @Query("""
        SELECT b
        FROM Book b
        WHERE b.isShareable = true
        AND b.user.id != :userId
    """)
    Page<Book> findAllDisplayableBooks(Pageable pageable, Long userId);

}
