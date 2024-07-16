package com.dustopia.book_social_network_api.service;

import com.dustopia.book_social_network_api.model.request.ChangePasswordRequest;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public interface UserService {

    void changePassword(ChangePasswordRequest changePasswordRequest, Principal connectedUser) throws IllegalAccessException;

}
