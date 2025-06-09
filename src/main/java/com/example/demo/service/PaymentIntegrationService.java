package com.example.demo.service;

import com.example.demo.dto.request.payment.CreateTransaction;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface PaymentIntegrationService {
    public Map<String, Object> createOrder(HttpServletRequest request, CreateTransaction createTransaction) throws UnsupportedEncodingException;
}