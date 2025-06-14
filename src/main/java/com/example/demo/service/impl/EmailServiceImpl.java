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

        this.coreSend(to, subject, body);
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


        this.coreSend(to, subject, body);
    }


    @Override
    public void sendLinkEmail(String to, String link) throws MessagingException {
        String subject = "Your Password Reset Link";
        String body = "<p>Dear User,</p>" +
                "<p>Click the link below to reset your password:</p>" +
                "<a href=\"" + link + "\">Reset Password</a>" +
                "<p>If you did not request this, please ignore this email.</p>" +
                "<p>Best regards,<br>LapShope</p>";

        this.coreSend(to, subject, body);
    }

    public void sendAccount(String to, String email, String password) throws MessagingException {
        String subject = "Your Account Credentials";
        String body = "<p>Dear User,</p>" +
                "<p>Your account has been created successfully.</p>" +
                "<p>Email: <b>" + email + "</b></p>" +
                "<p>Password: <b>" + password + "</b></p>" +
                "<p>Please keep your credentials safe and do not share them with anyone.</p>" +
                "<p>Best regards,<br>LapShope</p>";

        this.coreSend(to, subject, body);
    }

    private void coreSend(String to, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

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
