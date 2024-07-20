package com.dustopia.book_social_network_api;

import com.dustopia.book_social_network_api.config.CloudinaryConfiguration;
import com.dustopia.book_social_network_api.config.VNPayConfiguration;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;
import org.junit.jupiter.api.Test;

public class JwtSecretKeyGeneratorTest {

    @Test
    public void generateKey() {
        byte[] encoded = Jwts.SIG.HS512.key().build().getEncoded();
        String secretKey = DatatypeConverter.printHexBinary(encoded);
        System.out.printf("Generated secret key: [%s]", secretKey);
    }

}
