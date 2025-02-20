package com.example.demo.Service.Impl;

import com.example.demo.Common.Enums;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.Models.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.OrderService;
import com.example.demo.mapper.OrderMapper;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Map;


@Service

public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository; // Repository để kiểm tra Customer tồn tại
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;

    public OrderServiceImpl(PaymentRepository paymentRepository,OrderRepository orderRepository, CustomerRepository customerRepository,OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderDetailRepository=orderDetailRepository;
        this.paymentRepository=paymentRepository;
    }

    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(OrderMapper::convertToDTO)
                .collect(Collectors.toList());
    }


    @Override
    public OrderDTO getOrderById(UUID id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        return OrderMapper.convertToDTO(order);
    }

    @Transactional
    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Order order = Order.builder()
                .customer(customer)
                .dateCreate(LocalDateTime.now())
                .orderDetailList(null)
                .paymentList(null)
                .status(Enums.OrderStatus.Pending)
                .build();

        Order orderExisting = orderRepository.save(order);

        return OrderMapper.convertToDTO(orderExisting);
    }


    @Transactional
    @Override
    public OrderDTO updateOrder(UUID id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        existingOrder.setStatus(orderDTO.getStatus());
        existingOrder.setDateCreate(orderDTO.getDateCreate());
        //cap nhat danh sach orderdetail
        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
            existingOrder.getOrderDetailList().forEach(orderDetail -> orderDetail.setOrder(null));
        } else {
            List<OrderDetail> newOrderDetails = orderDTO.getOrderDetails().stream()
                    .map(orderDetailId -> orderDetailRepository.findById(orderDetailId)
                            .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found"))).toList();
            //loai bo nhung orderdetail khong co trong danh sach dto
            existingOrder.getOrderDetailList().removeIf(orderDetail -> !newOrderDetails.contains(orderDetail));
            //them nhung orderdetail moi
            newOrderDetails.forEach(orderDetail -> {
                if (!existingOrder.getOrderDetailList().contains(orderDetail)) {
                    orderDetail.setOrder(existingOrder);
                    existingOrder.getOrderDetailList().add(orderDetail);
                }
            });
        }

        if (orderDTO.getPayments() == null || orderDTO.getPayments().isEmpty()) {
            existingOrder.getPaymentList().forEach(orderDetail -> orderDetail.setOrder(null));
        } else {
            List<Payment> newPayments = orderDTO.getPayments().stream()
                    .map(paymentId -> paymentRepository.findById(paymentId)
                            .orElseThrow(() -> new EntityNotFoundException("Payment not found"))).toList();

            existingOrder.getPaymentList().removeIf(payment -> !newPayments.contains(payment));

            newPayments.forEach(payment -> {
                if (!existingOrder.getPaymentList().contains(payment)) {
                    payment.setOrder(existingOrder);
                    existingOrder.getPaymentList().add(payment);
                }
            });
        }

        Order order = orderRepository.save(existingOrder);

        return OrderMapper.convertToDTO(order);
    }

    @Override
    public OrderDTO partialUpdateOrder(UUID id, Map<String, Object> fieldsToUpdate) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with ID " + id + " not found!"));

        Class<?> clazz = order.getClass();

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
                            field.set(order, enumValue);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        }
                    } else {
                        field.set(order, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Order updatedOrder = orderRepository.save(order);
        return OrderMapper.convertToDTO(updatedOrder);
    }

    @Override
    public void deleteOrder(UUID id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        existingOrder.getOrderDetailList().forEach(orderDetail -> orderDetail.setOrder(null));
        existingOrder.getPaymentList().forEach(payment -> payment.setOrder(null));
        orderRepository.delete(existingOrder);
    }


}