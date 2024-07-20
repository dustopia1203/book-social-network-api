package com.dustopia.book_social_network_api.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public interface PaymentService {

    String createPaymentOrder(Long bookId, Authentication connectedUser) throws UnsupportedEncodingException;

    void confirmPayment(String responseCode, String orderId);

}
