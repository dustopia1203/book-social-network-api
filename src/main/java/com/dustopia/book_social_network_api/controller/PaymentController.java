package com.dustopia.book_social_network_api.controller;

import com.dustopia.book_social_network_api.model.response.ResponseObject;
import com.dustopia.book_social_network_api.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/book/{bookId}")
    public ResponseEntity<ResponseObject> createPaymentOrder(
            @PathVariable Long bookId,
            Authentication connectedUser
    ) throws UnsupportedEncodingException {
        String generatedPaymentUrl = paymentService.createPaymentOrder(bookId, connectedUser);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseObject("success", generatedPaymentUrl));
    }

    @GetMapping("/return-info")
    public ResponseEntity<?> handleReturnPaymentRequest(
            @RequestParam(value = "vnp_ResponseCode") String responseCode,
            @RequestParam(value = "vnp_TxnRef") String orderId
    ) {
        paymentService.confirmPayment(responseCode, orderId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .build();
    }

}
