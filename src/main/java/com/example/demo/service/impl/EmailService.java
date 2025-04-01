package com.example.demo.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Bean
    SimpleMailMessage templateBro() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("Test");
        message.setText("Test");

        return message;
    }


    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Handle the exception (e.g., log it)
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }

    public void sendOtpEmail(String to, String otp) throws MessagingException {
        String subject = "Your One-Time Password (OTP) Code";
        String body = "<p>Dear User,</p>" +
                "<p>Your One-Time Password (OTP) code is: <b>" + otp + "</b></p>" +
                "<p>Please use this code to complete your authentication process. " +
                "This code is valid for the next 10 minutes.</p>" +
                "<p>If you did not request this code, please ignore this email.</p>" +
                "<p>Best regards,<br>LapShope</p>";

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body, true);

        try {
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }


}
