package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.Enums;
import com.example.demo.dto.PaymentDTO;
import com.example.demo.dto.response.PaymentResponse;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.PaymentService;

import com.example.demo.mapper.PaymentMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {
    private final RedisService redisService;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final PaymentMethodRepository paymentMethodRepository;
    private final AccountRepository accountRepository;

    public PaymentServiceImpl(
            RedisService redisService,
            PaymentRepository paymentRepository,
            CustomerRepository customerRepository,
            OrderRepository orderRepository,
            PaymentMethodRepository paymentMethodRepository,
            AccountRepository accountRepository
    ) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.paymentMethodRepository = paymentMethodRepository;
        this.redisService = redisService;
        this.accountRepository = accountRepository;
    }

    @Override
    public List<PaymentResponse> getAllPaymentsByCustomer() {
        String currentUserEmail = AuthUtil.AuthCheck();

        Account account = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Account not found!"));


        List<PaymentResponse> paymentResponses = paymentRepository.findByCustomerId(account.getId())
                .stream()
                .map(PaymentMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allPaymentByCustomerId:" + currentUserEmail, paymentResponses, 600);

        return paymentResponses;
    }

    @Override
    public List<PaymentResponse> getAllPayments() {
        List<PaymentResponse> paymentResponses = paymentRepository.findAll().stream()
                .map(PaymentMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allPayment", paymentResponses, 600);

        return paymentResponses;
    }

    @Override
    public PaymentResponse getPaymentById(UUID id) {
        String currentUserEmail = AuthUtil.AuthCheck();
        Account account = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Account not found!"));
        PaymentResponse cachedPaymentResponses = redisService.getObject("payment:" + id, new TypeReference<PaymentResponse>() {
        });
        if (cachedPaymentResponses != null) {
            // Kiểm tra quyền truy cập
            if (!cachedPaymentResponses.getCustomer().equals(account.getId())) {
                throw new SecurityException("User is not authorized to view this payment");
            }
            return cachedPaymentResponses;
        }

        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment with ID " + id + " not found!"));

        PaymentResponse paymentResponse = PaymentMapper.convertToResponse(payment);

        redisService.setObject("payment:" + id, paymentResponse, 600);

        return paymentResponse;
    }

    @Transactional
    @Override
    public PaymentResponse createPayment(PaymentDTO paymentDTO) {
        String currentUserEmail = AuthUtil.AuthCheck();
        Account account = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Account not found!"));

        Customer customer = customerRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Payment Method not found!"));

        Payment payment = Payment.builder()
                .id(null)
                .type(paymentDTO.getType())
                .status(paymentDTO.getStatus())
                .paymentMethod(paymentMethod)
                .customer(customer)
                .order(order)
                .build();

        Payment paymentExisting = paymentRepository.save(payment);

        PaymentResponse cachedPayment = PaymentMapper.convertToResponse(paymentExisting);

        redisService.deleteByPatterns(List.of("allPayment", "allPaymentByCustomerId:" + account.getId()));
        redisService.setObject("payment:" + account.getId(), cachedPayment, 600);

        return cachedPayment;
    }

    @Transactional
    @Override
    public PaymentResponse updatePayment(UUID id, PaymentDTO paymentDTO) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(existingPayment.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this payment");
        }

        Order order = orderRepository.findById(paymentDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));
        existingPayment.setOrder(order);

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentDTO.getPaymentMethodId())
                .orElseThrow(() -> new EntityNotFoundException("Payment Method not found!"));

//        existingPayment.setCustomer(customer);
        existingPayment.setType(paymentDTO.getType());
        existingPayment.setStatus(paymentDTO.getStatus());

        Payment payment = paymentRepository.save(existingPayment);

        PaymentResponse cachedPaymentResponse = PaymentMapper.convertToResponse(payment);

        redisService.deleteByPatterns(List.of("allPayment", "payment:" + id, "allPaymentByCustomerId:+id"));
        redisService.setObject("payment:" + id, cachedPaymentResponse, 600);

        return cachedPaymentResponse;
    }

    @Transactional
    @Override
    public PaymentResponse partialUpdatePayment(UUID id, Map<String, Object> fieldsToUpdate) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment with ID " + id + " not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(payment.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

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
                            Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString());
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
        PaymentResponse cachedPaymentResponse = PaymentMapper.convertToResponse(updatedPayment);

        redisService.deleteByPatterns(List.of("allPayment", "payment:" + id, "allPaymentByCustomerId:+id"));
        redisService.setObject("payment:" + id, cachedPaymentResponse, 600);

        return cachedPaymentResponse;
    }


    @Transactional
    @Override
    public void deletePayment(UUID id) {
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(existingPayment.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        redisService.deleteByPatterns(List.of("allPayment", "payment:" + id, "allPaymentByCustomerId:+id"));

        paymentRepository.delete(existingPayment);
    }

    @Override
    public void handlePayment(Map<String, String> params) {
        String vnpResponseCode = params.get("vnp_ResponseCode");
        String vnpTxnRef = params.get("vnp_TxnRef");
        String vnpAmount = params.get("vnp_Amount");
        String vnpOrderInfo = params.get("vnp_OrderInfo");
        String vnpTransactionNo = params.get("vnp_TransactionNo");
        String vnpTransactionStatus = params.get("vnp_TransactionStatus");
        String vnpPayDate = params.get("vnp_PayDate");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        System.out.printf(
                "vnp_ResponseCode: %s, vnp_TxnRef: %s, vnp_Amount: %s, vnp_OrderInfo: %s, vnp_TransactionNo: %s, vnp_TransactionStatus: %s, vnp_PayDate: %s%n",
                vnpResponseCode, vnpTxnRef, vnpAmount, vnpOrderInfo, vnpTransactionNo, vnpTransactionStatus, vnpPayDate
        );

//        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef).orElseThrow(() -> {
//            throw new RuntimeException("Payment not found");
//        });
        Optional<Order> order = orderRepository.findById(UUID.fromString(vnpTxnRef));
        if (order.isEmpty()) {
            throw new RuntimeException("Order not found for transaction reference: " + vnpTxnRef);
        }

//        PaymentMethod tmp = paymentMethodRepository.save(PaymentMethod.builder()
//                .id(UUID.fromString(vnpTxnRef))
//                .data(Map.of(
//                        "vnp_ResponseCode", vnpResponseCode,
//                        "vnp_TxnRef", vnpTxnRef,
//                        "vnp_Amount", vnpAmount,
//                        "vnp_OrderInfo", vnpOrderInfo,
//                        "vnp_TransactionNo", vnpTransactionNo,
//                        "vnp_TransactionStatus", vnpTransactionStatus,
//                        "vnp_PayDate", vnpPayDate
//                ))
//                .type(Enums.PaymentType.VNPay)
//                .build());
        Optional<PaymentMethod> tmp = paymentMethodRepository.findById(UUID.fromString("110d2134-9a49-42cb-9053-c458bdf97283"));
        if (tmp.isEmpty()) {
            throw new RuntimeException("Payment Method not found for ID: 110d2134-9a49-42cb-9053-c458bdf97283");
        }

        Payment payment = Payment.builder()
                .order(order.get())
                .paymentMethod(null)
                .type(Enums.PaymentType.VNPay)
                .customer(order.get().getCustomer())
                .paymentMethod(tmp.get())
                .type(Enums.PaymentType.VNPay)
                .build();

        try {
            switch (vnpResponseCode) {
                case "00":
                    payment.setStatus(Enums.PaymentStatus.Success);
                    break;
                case "01":
                    payment.setStatus(Enums.PaymentStatus.Pending);
                    break;
                default:
                    payment.setStatus(Enums.PaymentStatus.Failed);
//                    RedisServiceImpl.del("pickseat:" + payment.getPerform().getId() + ":" + payment.getAccount().getId());
                    break;
            }

            paymentRepository.save(payment);

        } catch (Exception e) {
            e.printStackTrace();
            paymentRepository.save(payment);

            throw new RuntimeException("Error: " + e.getMessage());
        }
    }
}