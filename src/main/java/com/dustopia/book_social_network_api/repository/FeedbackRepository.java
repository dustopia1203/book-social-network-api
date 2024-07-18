package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("""
        SELECT fb
        FROM Feedback fb
        WHERE fb.book.id = :id
    """)
    Page<Feedback> findAllFeedbackByBook(Long id, Pageable pageable);

}
