package com.example.demo.service;

import com.example.demo.dto.PaymentDTO;
import com.example.demo.dto.response.PaymentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    List<PaymentResponse> getAllPaymentsByCustomer();
    PaymentResponse partialUpdatePayment(UUID id, Map<String,Object> fieldsToUpdate);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(UUID id);
    PaymentResponse createPayment(PaymentDTO paymentDTO);
    PaymentResponse updatePayment(UUID id, PaymentDTO paymentDTO);
    void deletePayment(UUID id);
}