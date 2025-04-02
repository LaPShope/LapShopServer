package com.example.demo.service.impl;

import com.example.demo.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

        try {
            message.setFrom("noreply@lapshope.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body, true);

            mailSender.send(mimeMessage);
        } catch (MailException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }


    @Override
    public void sendOTTEmail(String to, String ott) throws MessagingException {
        String subject = "Your One-Time Token (OTT) Code";
        String body = "<p>Dear User,</p>" +
                "<p>Your One-Time Token (OTT) code is: <b>" + ott + "</b></p>" +
                "<p>Please use this code to complete your authentication process. " +
                "This code is valid for the next 10 minutes.</p>" +
                "<p>If you did not request this code, please ignore this email.</p>" +
                "<p>Best regards,<br>LapShope</p>";


        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

        try {
            helper.setFrom("noreply@lapshope.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(mimeMessage);
        } catch (MailException e) {
            System.err.println("Error sending email: " + e.getMessage());
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
