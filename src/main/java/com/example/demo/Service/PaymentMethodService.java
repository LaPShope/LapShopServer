package com.example.demo.Service;

import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.PaymentMethodDTO;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PaymentMethodService {
    PaymentMethodDTO partialUpdatePaymentMethod(UUID id, Map<String,Object> fieldsToUpdate);

    List<PaymentMethodDTO> getAllPaymentMethods();

    PaymentMethodDTO getPaymentMethodById(UUID id);

    PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO);

    PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO paymentMethodDTO);

    void deletePaymentMethod(UUID id);
}
