package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.ConvertDate;
import com.example.demo.common.ConvertSnakeToCamel;
import com.example.demo.common.Enums;
import com.example.demo.dto.OrderDTO;
import com.example.demo.dto.response.order.OrderResponse;
import com.example.demo.model.*;
import com.example.demo.repository.*;
import com.example.demo.service.OrderService;
import com.example.demo.mapper.OrderMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import io.netty.util.AbstractReferenceCounted;


@Service

public class OrderServiceImpl implements OrderService {
    private final RedisService redisService;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository; // Repository để kiểm tra Customer tồn tại
    private final OrderDetailRepository orderDetailRepository;
    private final PaymentRepository paymentRepository;
    private final CartRepository cartRepository; // Repository để kiểm tra Cart tồn tại
    private final AccountRepository accountRepository; // Repository để kiểm tra Account tồn tại

    public OrderServiceImpl(CartRepository cartRepository, AccountRepository accountRepository, RedisService redisService, PaymentRepository paymentRepository, OrderRepository orderRepository, CustomerRepository customerRepository, OrderDetailRepository orderDetailRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.paymentRepository = paymentRepository;
        this.redisService = redisService;
        this.accountRepository = accountRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public List<OrderResponse> getAllOrders() {
        String currentUserEmail = AuthUtil.AuthCheck();

        List<OrderResponse> orderResponses = orderRepository.findAll().stream()
                .filter(order -> order.getCustomer().getAccount().getEmail().equals(currentUserEmail))
                .map(OrderMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allOrder" + currentUserEmail, orderResponses, 600);

        return orderResponses;
    }

    @Override
    public OrderResponse getOrderById(UUID id) {
        OrderResponse cachedOrderResponse = redisService.getObject("order:" + id, new TypeReference<OrderResponse>() {
        });
        ;
        if (cachedOrderResponse != null) {
            return cachedOrderResponse;
        }

        String currentUserEmail = AuthUtil.AuthCheck();

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        if (!order.getCustomer().getAccount().getEmail().equals(currentUserEmail)) {
            throw new SecurityException("User is not authorized to view this order");
        }

        OrderResponse orderResponse = OrderMapper.convertToResponse(order);

        redisService.setObject("order:" + id, orderResponse, 600);

        return orderResponse;
    }

    @Transactional
    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) {
        String currentUserEmail = AuthUtil.AuthCheck();

        Account account = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Account not found!"));

        Customer customer = customerRepository.findById(account.getId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        List<Cart> cart = customer.getCartList();

        Order order = Order.builder()
                .customer(customer)
                .dateCreate(new Date())
                .orderDetailList(null)
                .payment(null)
                .address(orderDTO.getAddress())
                .deliveryCost(orderDTO.getDeliveryCost())
                .finalPrice(orderDTO.getFinalPrice())
                .status(Enums.OrderStatus.Pending)
                .build();

        if (cart != null) {
            for (Cart item : cart) {
                List<OrderDetail> items = item.getLaptopOnCarts().stream().map(laptopOnCart -> {
                    BigDecimal price = laptopOnCart.getLaptopModel().getPrice();
                    BigDecimal quantity = new BigDecimal(laptopOnCart.getQuantity());
                    BigDecimal totalItemPrice = price.multiply(quantity); // Multiply price by quantity

                    OrderDetail orderDetail = OrderDetail.builder()
                            .id(null)
                            .order(order)
                            .laptopModel(laptopOnCart.getLaptopModel())
                            .quantity(laptopOnCart.getQuantity())
                            .price(totalItemPrice)
                            .build();
                    return orderDetail;
                }).collect(Collectors.toList());

                order.setOrderDetailList(items);
            }
        }

        BigDecimal recalculatedTotalPrice = order.getOrderDetailList().stream()
                .map(orderDetail -> orderDetail.getPrice().multiply(new BigDecimal(orderDetail.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        order.setFinalPrice(recalculatedTotalPrice.add(order.getDeliveryCost()));

        Order orderExisting = orderRepository.save(order);

        OrderResponse cachedOrderResponse = OrderMapper.convertToResponse(orderExisting);

        redisService.deleteByPatterns(List.of("allOrder"));
        redisService.setObject("order:" + orderDTO.getId(), cachedOrderResponse, 600);

        return cachedOrderResponse;
    }


    @Transactional
    @Override
    public OrderResponse updateOrder(UUID id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(existingOrder.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        existingOrder.setStatus(orderDTO.getStatus());
        Order order = orderRepository.save(existingOrder);

        OrderResponse cachedorderResponse = OrderMapper.convertToResponse(order);

        redisService.deleteByPatterns(List.of("allOrder", "*der*"));
        redisService.setObject("order:" + id, cachedorderResponse, 600);

        return cachedorderResponse;
    }

    @Override
    public OrderResponse partialUpdateOrder(UUID id, Map<String, Object> fieldsToUpdate) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order with ID " + id + " not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(order.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        Class<?> clazz = order.getClass();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            if (fieldName.equals("date_create")) {
                fieldName = ConvertSnakeToCamel.snakeToCamel(fieldName);
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

        redisService.deleteByPatterns(List.of("allOrder", "*der*"));
        redisService.setObject("order:" + id, cachedorderResponse, 600);

        return cachedorderResponse;
    }

    @Override
    public void deleteOrder(UUID id) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(existingOrder.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to delete this order");
        }

        redisService.deleteByPatterns(List.of("allOrder", "*der*"));

        orderRepository.delete(existingOrder);
    }


}