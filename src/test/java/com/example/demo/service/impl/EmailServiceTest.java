package com.example.demo.service.impl;


import com.example.demo.service.EmailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.UUID;

import static org.mockito.Mockito.doThrow;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    JavaMailSender mailSender;

    @Autowired
    EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(mailSender);
    }


    @Test
    void Check_SendMail_ThenSucess() throws MessagingException {

        emailService.sendOtpEmail("ntriet0612@gmail.com", "123456");

        doThrow(new RuntimeException("Failed to send email"))
                .when(mailSender)
                .send(Mockito.any(SimpleMailMessage.class));


        try {
            emailService.sendOtpEmail("ntriet0612@gmail.com", "123456");
        } catch (RuntimeException e) {
            // Handle the exception (e.g., log it)
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    @Test
    void Check_SendLinkViaGmail_ThenSucess() throws MessagingException {
        String baseClientUrl = "http://localhost:3000/reset-password?token=";
        String token = UUID.randomUUID().toString();

//        redisService.set(email, token.toString(), 60 * 3);

        try {
            this.emailService.sendLinkEmail("ntriet0612@gmail.com", baseClientUrl + token);
        }
        catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
