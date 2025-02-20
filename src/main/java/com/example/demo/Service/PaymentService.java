package com.example.demo.Service;

import com.example.demo.DTO.PaymentDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaymentService {
    PaymentDTO partialUpdatePayment(UUID id, Map<String,Object> fieldsToUpdate);
    List<PaymentDTO> getAllPayments();
    PaymentDTO getPaymentById(UUID id);
    PaymentDTO createPayment(PaymentDTO paymentDTO);
    PaymentDTO updatePayment(UUID id, PaymentDTO paymentDTO);
    void deletePayment(UUID id);
}