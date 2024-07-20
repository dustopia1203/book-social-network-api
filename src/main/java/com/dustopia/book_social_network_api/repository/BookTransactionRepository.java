package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {

    @Query("""
        SELECT bt
        FROM BookTransaction bt
        WHERE bt.user.id = :userId
    """)
    Page<BookTransaction> findAllPurchasedBooks(Long userId, Pageable pageable);

    boolean existsByUserAndBook(User user, Book book);

}
