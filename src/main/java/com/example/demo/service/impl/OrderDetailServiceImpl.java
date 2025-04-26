package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.dto.OrderDetailDTO;
import com.example.demo.dto.response.OrderDetailResponse;
import com.example.demo.model.LaptopModel;
import com.example.demo.model.Order;
import com.example.demo.model.OrderDetail;
import com.example.demo.repository.LaptopModelRepository;
import com.example.demo.repository.OrderDetailRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.service.OrderDetailService;
import com.example.demo.mapper.OrderDetailMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderDetailServiceImpl implements OrderDetailService {
    private final RedisService redisService;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final LaptopModelRepository laptopModelRepository;

    public OrderDetailServiceImpl(RedisService redisService, OrderDetailRepository orderDetailRepository,
                                  OrderRepository orderRepository,
                                  LaptopModelRepository laptopModelRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
    }

    @Override
    public List<OrderDetailResponse> getAllOrderDetailsByAccount() {
        String currentUserEmail = AuthUtil.AuthCheck();

        List<OrderDetailResponse> cachedOrderDetail = redisService.getObject("allOrderDetailAccount"+currentUserEmail, new TypeReference<List<OrderDetailResponse>>() {});
        if (cachedOrderDetail != null && !cachedOrderDetail.isEmpty()){
            return cachedOrderDetail;
        }

        List<OrderDetailResponse> orderDetailResponses = orderDetailRepository.findAll().stream()
                .filter(orderDetail -> orderDetail.getOrder().getCustomer().getAccount().getEmail().equals(currentUserEmail))
                .map(OrderDetailMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allOrderDetailAccount"+currentUserEmail,orderDetailResponses,600);

        return orderDetailResponses;
    }
    
    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        List<OrderDetailResponse> cachedOrderDetail = redisService.getObject("allOrderDetail", new TypeReference<List<OrderDetailResponse>>() {});
        if (cachedOrderDetail != null && !cachedOrderDetail.isEmpty()){
            return cachedOrderDetail;
        }

        List<OrderDetailResponse> orderDetailResponses = orderDetailRepository.findAll().stream()
                .map(OrderDetailMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allOrderDetail",orderDetailResponses,600);

        return orderDetailResponses;
    }

    @Override
    public OrderDetailResponse getOrderDetailById(UUID id) {
        OrderDetailResponse cachedOrderDetail = redisService.getObject("orderDetail:"+id, new TypeReference<OrderDetailResponse>() {});
        if (cachedOrderDetail != null){
            return cachedOrderDetail;
        }

        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found"));

        OrderDetailResponse orderDetailResponse = OrderDetailMapper.convertToResponse(orderDetail);

        redisService.setObject("orderDetail:"+id,orderDetailResponse,600);

        return orderDetailResponse;
    }

    @Transactional
    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) { 
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order  not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(order.getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to create this orderDetail");
        }

        LaptopModel laptopModel = laptopModelRepository.findById(orderDetailDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found!"));

        OrderDetail orderDetail = OrderDetail.builder()
                .id(null)
                .order(order)
                .laptopModel(laptopModel)
                .quantity(orderDetailDTO.getQuantity())
                .price(orderDetailDTO.getPrice())
                .build();

        OrderDetail orderDetailExisting = orderDetailRepository.save(orderDetail);

        OrderDetailResponse cachedOrderDetailResponse = OrderDetailMapper.convertToResponse(orderDetailExisting);

        redisService.deleteByPatterns(List.of("allOrderDetail","allOrder","order:"+cachedOrderDetailResponse.getOrder().getId()));
        redisService.setObject("orderDetail:"+cachedOrderDetailResponse.getId(),cachedOrderDetailResponse,600);

        return cachedOrderDetailResponse;
    }

    @Transactional
    @Override
    public OrderDetailResponse updateOrderDetail(UUID id, OrderDetailDTO orderDetailDTO) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(existingOrderDetail.getOrder().getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        LaptopModel laptopModel = laptopModelRepository.findById(orderDetailDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found!"));

        existingOrderDetail.setOrder(order);
        existingOrderDetail.setLaptopModel(laptopModel);
        existingOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());

        OrderDetail orderDetailExisting = orderDetailRepository.save(existingOrderDetail);

        OrderDetailResponse cachedOrderDetailResponse = OrderDetailMapper.convertToResponse(orderDetailExisting);

        redisService.deleteByPatterns(List.of("allOrderDetail","allOrder","orderDetail:"+id,"order:"+cachedOrderDetailResponse.getOrder().getId()));
        redisService.setObject("orderDetail:"+id,cachedOrderDetailResponse,600);

        return cachedOrderDetailResponse;
    }

    @Transactional
    @Override
    public OrderDetailResponse partialUpdateOrderDetail(UUID id, Map<String, Object> fieldsToUpdate) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail with ID " + id + " not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(orderDetail.getOrder().getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        Class<?> clazz = orderDetail.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null ) {
                    if (field.getType().equals(BigDecimal.class)) {
                        field.set(orderDetail, new BigDecimal(newValue.toString()));
                    } else if (field.getType().equals(Integer.class)) {
                        field.set(orderDetail, Integer.parseInt(newValue.toString()));
                    } else {
                        field.set(orderDetail, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }
        OrderDetail updatedOrderDetail = orderDetailRepository.save(orderDetail);
        OrderDetailResponse cachedOrderDetailResponse = OrderDetailMapper.convertToResponse(updatedOrderDetail);

        redisService.deleteByPatterns(List.of("allOrderDetail","allOrder","orderDetail:"+id,"order:"+cachedOrderDetailResponse.getOrder().getId()));
        redisService.setObject("orderDetail:"+id,cachedOrderDetailResponse,600);

        return cachedOrderDetailResponse;
    }

    @Transactional
    @Override
    public void deleteOrderDetail(UUID id) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(existingOrderDetail.getOrder().getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to update this orderDetail");
        }

        redisService.deleteByPatterns(List.of("allOrderDetail","allOrder","orderDetail:"+id,"order:"+existingOrderDetail.getOrder().getId()));

        orderDetailRepository.delete(existingOrderDetail);
    }


}