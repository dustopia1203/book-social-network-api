package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}
