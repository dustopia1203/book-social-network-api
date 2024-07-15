package com.dustopia.book_social_network_api.service.impl;

import com.dustopia.book_social_network_api.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    private final SpringTemplateEngine templateEngine;

    @Override
    @Async
    public void sendEmail(
            String to,
            String username,
            String subject,
            String emailTemplate,
            String confirmationUrl,
            String activationCode
    ) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()
        );
        helper.setFrom("contact@dustopia.com");
        helper.setTo(to);
        helper.setSubject(subject);

        Map<String, Object> properties = new HashMap<>();
        properties.put("username", username);
        properties.put("confirmation_url", confirmationUrl);
        properties.put("activation_code", activationCode);

        Context context = new Context();
        context.setVariables(properties);

        String template = templateEngine.process(emailTemplate, context);

        helper.setText(template, true);

        mailSender.send(mimeMessage);
    }

}
