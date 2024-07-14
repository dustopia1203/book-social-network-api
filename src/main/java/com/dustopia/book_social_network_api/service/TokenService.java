package com.dustopia.book_social_network_api.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {

    void revokeAllTokensOfUser(String email);

    void saveJwtToken(String email, String token);

}
