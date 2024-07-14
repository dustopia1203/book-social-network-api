package com.dustopia.book_social_network_api.service;

import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {

    void sendEmail(
            String to,
            String username,
            String subject,
            String emailTemplate,
            String confirmationUrl,
            String activationCode
    ) throws MessagingException;

}
