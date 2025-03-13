package com.example.demo.Service.Impl;

import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.ConvertSnakeToCamel;
import com.example.demo.Common.Enums;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.Response.OrderResponse.OrderResponse;
import com.example.demo.Models.*;
import com.example.demo.Repository.*;
import com.example.demo.Service.OrderService;
import com.example.demo.mapper.OrderMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Map;


@Service

public class OrderServiceImpl implements OrderService {
    private final RedisService redisService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository; // Repository để kiểm tra Customer tồn tại
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;

    public OrderServiceImpl(RedisService redisService, PaymentRepository paymentRepository,OrderRepository orderRepository, CustomerRepository customerRepository,OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderDetailRepository=orderDetailRepository;
        this.paymentRepository=paymentRepository;
        this.redisService = redisService;
    }

    public List<OrderResponse> getAllOrders() {
        List<OrderResponse> cachedOrderResponses = redisService.getObject("allOder", new TypeReference<List<OrderResponse>>() {});
        if(cachedOrderResponses != null && !cachedOrderResponses.isEmpty()){
            return cachedOrderResponses;
        }

        List<OrderResponse> orderResponses = orderRepository.findAll().stream()
                .map(OrderMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allOrder",orderResponses,600);

        return orderResponses;
    }


    @Override
    public OrderResponse getOrderById(UUID id) {
        OrderResponse cachedOrderResponse = redisService.getObject("order:" + id, new TypeReference<OrderResponse>() {});;
        if (cachedOrderResponse != null){
            return cachedOrderResponse;
        }

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        OrderResponse orderResponse = OrderMapper.convertToResponse(order);

        redisService.setObject("order:"+id,orderResponse,600);

        return orderResponse;
    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) {
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Order order = Order.builder()
                .customer(customer)
                .dateCreate(new Date())
                .orderDetailList(null)
                .payment(null)
                .status(Enums.OrderStatus.Pending)
                .build();

        Order orderExisting = orderRepository.save(order);

        OrderResponse cachedOrderResponse = OrderMapper.convertToResponse(orderExisting);

        redisService.deleteByPatterns(List.of("allOrder"));
        redisService.setObject("oder:"+orderDTO.getId(),cachedOrderResponse,600);

        return cachedOrderResponse;
    }


    @Transactional
    @Override
    public OrderResponse updateOrder(UUID id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        existingOrder.setStatus(orderDTO.getStatus());
        existingOrder.setDateCreate(orderDTO.getDateCreate());
//        //cap nhat danh sach orderdetail
//        if (orderDTO.getOrderDetails() == null || orderDTO.getOrderDetails().isEmpty()) {
//            existingOrder.getOrderDetailList().forEach(orderDetail -> orderDetail.setOrder(null));
//        } else {
//            List<OrderDetail> newOrderDetails = orderDTO.getOrderDetails().stream()
//                    .map(orderDetailId -> orderDetailRepository.findById(orderDetailId)
//                            .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found"))).toList();
//            //loai bo nhung orderdetail khong co trong danh sach dto
//            existingOrder.getOrderDetailList().removeIf(orderDetail -> !newOrderDetails.contains(orderDetail));
//            //them nhung orderdetail moi
//            newOrderDetails.forEach(orderDetail -> {
//                if (!existingOrder.getOrderDetailList().contains(orderDetail)) {
//                    orderDetail.setOrder(existingOrder);
//                    existingOrder.getOrderDetailList().add(orderDetail);
//                }
//            });
//        }

//        if (orderDTO.getPayments() == null || orderDTO.getPayments().isEmpty()) {
//            existingOrder.getPayment().setOrder(null);
//        } else {
//            Payment newPayments =  paymentRepository.findById(orderDTO.getCustomerId())
//                            .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
//
//            existingOrder.setPayment(newPayments);
//
//        }

        Order order = orderRepository.save(existingOrder);

        OrderResponse cachedorderResponse =  OrderMapper.convertToResponse(order);

        redisService.deleteByPatterns(List.of("allOder","*der*"));
        redisService.setObject("oder:"+id,cachedorderResponse,600);

        return cachedorderResponse;
    }

    @Override
    public OrderResponse partialUpdateOrder(UUID id, Map<String, Object> fieldsToUpdate) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with ID " + id + " not found!"));

        Class<?> clazz = order.getClass();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Định dạng chuẩn ISO 8601

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName.equals("date_create")){
                fieldName= ConvertSnakeToCamel.snakeToCamel(fieldName);
            }
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
                    } else if (field.getType().equals(Date.class)) {
                            Date parsedDate = ConvertDate.convertToDate(newValue);
                            field.set(order, parsedDate);
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
        OrderResponse cachedorderResponse = OrderMapper.convertToResponse(updatedOrder);

        redisService.deleteByPatterns(List.of("allOder","*der*"));
        redisService.setObject("oder:"+id,cachedorderResponse,600);

        return cachedorderResponse;
    }

    @Override
    public void deleteOrder(UUID id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        redisService.deleteByPatterns(List.of("allOder","*der*"));

        orderRepository.delete(existingOrder);
    }


}