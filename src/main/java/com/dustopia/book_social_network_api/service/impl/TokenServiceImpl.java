package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.model.entity.Token;
import com.dustopia.book_social_network_api.repository.TokenRepository;
import com.dustopia.book_social_network_api.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;

    @Override
    public void revokeAllTokensOfUser(String email) {
        List<Token> tokens = tokenRepository.findAllValidToken(email);
        if (tokens.isEmpty()) return;
        tokens.forEach(token -> token.setRevoked(true));
        tokenRepository.saveAll(tokens);
    }

    @Override
    public void saveJwtToken(String email, String token) {
        tokenRepository.save(Token
                .builder()
                .email(email)
                .type("BEARER")
                .token(token)
                .isRevoked(false)
                .build()
        );
    }

}
