package com.dustopia.book_social_network_api.repository;

import com.dustopia.book_social_network_api.model.entity.Token;
import com.dustopia.book_social_network_api.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    Optional<Token> findByToken(String token);

    @Query("""
        SELECT t 
        FROM Token t 
        WHERE t.email = :email AND t.isRevoked = FALSE 
    """)
    List<Token> findAllValidToken(String email);

}
