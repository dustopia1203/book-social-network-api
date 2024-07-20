package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.Optional;

public interface BookTransactionRepository extends JpaRepository<BookTransaction, Long> {

    @Query("""
        SELECT bt
        FROM BookTransaction bt
        WHERE bt.user.id = :userId
        AND bt.isPurchased = true
    """)
    Page<BookTransaction> findAllPurchasedBooks(Long userId, Pageable pageable);

    @Query("""
        SELECT CASE WHEN COUNT(bt) > 0 
            THEN true 
            ELSE false 
        END 
        FROM BookTransaction bt 
        WHERE bt.user = :user 
        AND bt.book = :book 
        AND bt.isPurchased = true
    """)
    boolean isPurchased(User user, Book book);

    @Query("""
        SELECT bt FROM BookTransaction bt 
        WHERE bt.orderId = :orderId 
        AND FUNCTION('DATE', bt.createdDate) = FUNCTION('DATE', :currentDate)
    """)
    Optional<BookTransaction> findPaymentByOrderId(String orderId, LocalDate currentDate);

    Optional<BookTransaction> findByUserAndBook(User user, Book book);

}
