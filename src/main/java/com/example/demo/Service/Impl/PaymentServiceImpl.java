package com.example.demo.Service.Impl;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.PaymentDTO;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Models.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.PaymentService;

import com.example.demo.mapper.PaymentMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentServiceImpl(
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            PaymentMethodRepository paymentMethodRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(PaymentMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentDTO getPaymentById(UUID id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment with ID " + id + " not found!"));

        return PaymentMapper.convertToDTO(payment);
    }

    @Override
    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Customer customer = customerRepository.findById(paymentDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Payment not found!"));

        Payment payment = Payment.builder()
                .id(null)
                .type(paymentDTO.getType())
                .status(paymentDTO.getStatus())
                .paymentMethod(paymentMethod)
                .customer(customer)
                .order(order)
                .build();

        Payment paymentExisting = paymentRepository.save(payment);

        return PaymentMapper.convertToDTO(paymentExisting);
    }

    @Override
    public PaymentDTO updatePayment(UUID id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found!"));

        Customer customer = customerRepository.findById(paymentDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));
        existingPayment.setCustomer(customer);

        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));
        existingPayment.setOrder(order);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Payment Method not found!"));

        existingPayment.setCustomer(customer);
        existingPayment.setOrder(order);
        existingPayment.setPaymentMethod(paymentMethod);
        existingPayment.setType(paymentDTO.getType());
        existingPayment.setStatus(paymentDTO.getStatus());

        Payment payment = paymentRepository.save(existingPayment);

        return PaymentMapper.convertToDTO(payment);
    }

    @Override
    public PaymentDTO partialUpdatePayment(UUID id, Map<String, Object> fieldsToUpdate) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment with ID " + id + " not found!"));

        Class<?> clazz = payment.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().isEnum()) {
                        try {
                            Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString().toUpperCase());
                            field.set(payment, enumValue);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value '" + newValue + "' for field: " + fieldName);
                        }
                    } else {
                        field.set(payment, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentMapper.convertToDTO(updatedPayment);
    }


    @Override
    public void deletePayment(UUID id) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found!"));

        paymentRepository.delete(existingPayment);
    }


}