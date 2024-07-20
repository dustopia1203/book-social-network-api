package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.config.VNPayConfiguration;
import com.dustopia.book_social_network_api.exception.BookUnavailableException;
import com.dustopia.book_social_network_api.exception.OperationNotPermittedException;
import com.dustopia.book_social_network_api.exception.PermissionDeniedAccessException;
import com.dustopia.book_social_network_api.model.entity.Book;
import com.dustopia.book_social_network_api.model.entity.BookTransaction;
import com.dustopia.book_social_network_api.model.entity.User;
import com.dustopia.book_social_network_api.repository.BookRepository;
import com.dustopia.book_social_network_api.repository.BookTransactionRepository;
import com.dustopia.book_social_network_api.security.CustomUserDetails;
import com.dustopia.book_social_network_api.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BookRepository bookRepository;

    private final BookTransactionRepository bookTransactionRepository;

    @Override
    public String createPaymentOrder(Long bookId, Authentication connectedUser) throws UnsupportedEncodingException {
        User user = ((CustomUserDetails) connectedUser.getPrincipal()).getUser();
        Book book = bookRepository
                .findById(bookId)
                .orElseThrow(() -> new EntityNotFoundException("Book is not found with id " + bookId));
        BookTransaction bookTransaction = bookTransactionRepository
                .findByUserAndBook(user, book)
                .orElse(null);
        if (!book.isShareable()) {
            throw new BookUnavailableException("Book is unavailable");
        }
        if (bookTransaction != null && bookTransaction.isPurchased()) {
            throw new OperationNotPermittedException("Current user already purchased book");
        }

        String vnp_Version = "2.1.0";
        String vnp_Command = "pay";
        String orderType = "other";
        long amount = 10000*100;
//        String bankCode = req.getParameter("bankCode");
        String bankCode = "NCB";
        String vnp_TxnRef = VNPayConfiguration.getRandomNumber(8);
//        String vnp_IpAddr = VNPayConfiguration.getIpAddress(req);
        String vnp_IpAddr = "0.0.0.1";

        String vnp_TmnCode = VNPayConfiguration.vnp_TmnCode;

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnp_Version);
        vnp_Params.put("vnp_Command", vnp_Command);
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");

        if (bankCode != null && !bankCode.isEmpty()) {
            vnp_Params.put("vnp_BankCode", bankCode);
        }
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);

//        String locate = req.getParameter("language");
        String locate = "vn";
        if (locate != null && !locate.isEmpty()) {
            vnp_Params.put("vnp_Locale", locate);
        } else {
            vnp_Params.put("vnp_Locale", "vn");
        }
        vnp_Params.put("vnp_ReturnUrl", VNPayConfiguration.vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List fieldNames = new ArrayList(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                //Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                //Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayConfiguration.hmacSHA512(VNPayConfiguration.secretKey, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VNPayConfiguration.vnp_PayUrl + "?" + queryUrl;

        if (bookTransaction != null) {
            bookTransaction.setOrderId(vnp_TxnRef);
            bookTransactionRepository.save(bookTransaction);
        } else {
            BookTransaction newBookTransaction = BookTransaction
                    .builder()
                    .user(user)
                    .book(book)
                    .orderId(vnp_TxnRef)
                    .isPurchased(false)
                    .build();
            bookTransactionRepository.save(newBookTransaction);
        }

        return paymentUrl;
    }

    @Override
    public void confirmPayment(String responseCode, String orderId) {
        if (!responseCode.equals("00")) {
            throw new RuntimeException("Payment failed");
        }
        BookTransaction bookTransaction = bookTransactionRepository
                .findPaymentByOrderId(orderId, LocalDate.now())
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        bookTransaction.setPurchased(true);
        bookTransactionRepository.save(bookTransaction);
    }

}
