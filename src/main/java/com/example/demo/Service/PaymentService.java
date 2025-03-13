package com.example.demo.Service;

import com.example.demo.DTO.PaymentDTO;
import com.example.demo.DTO.Response.PaymentResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    List<PaymentResponse> getAllPaymentsByCustomerId(UUID id);
    PaymentResponse partialUpdatePayment(UUID id, Map<String,Object> fieldsToUpdate);
    List<PaymentResponse> getAllPayments();
    PaymentResponse getPaymentById(UUID id);
    PaymentResponse createPayment(PaymentDTO paymentDTO);
    PaymentResponse updatePayment(UUID id, PaymentDTO paymentDTO);
    void deletePayment(UUID id);
}