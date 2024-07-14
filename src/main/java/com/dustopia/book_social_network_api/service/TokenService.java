package com.dustopia.book_social_network_api.service;

import org.springframework.stereotype.Service;

@Service
public interface TokenService {

    void revokeAllTokensOfUser(String email);

    void saveToken(String username, String token);

}
