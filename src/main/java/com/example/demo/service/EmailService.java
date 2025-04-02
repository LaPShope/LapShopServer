package com.example.demo.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendOtpEmail(String to, String otp) throws MessagingException;
    void sendOTTEmail(String to, String ott) throws MessagingException;
}
