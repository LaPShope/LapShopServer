package com.example.demo.Service.Impl;

import com.example.demo.DTO.ImageDTO;
import com.example.demo.DTO.OrderDTO;
import com.example.demo.DTO.OrderDetailDTO;
import com.example.demo.DTO.Response.OrderDetailResponse;
import com.example.demo.Models.Image;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Models.Order;
import com.example.demo.Models.OrderDetail;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Repository.OrderDetailRepository;
import com.example.demo.Repository.OrderRepository;
import com.example.demo.Service.OrderDetailService;
import com.example.demo.mapper.OrderDetailMapper;
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

    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final LaptopModelRepository laptopModelRepository;

    public OrderDetailServiceImpl(OrderDetailRepository orderDetailRepository,
                                  OrderRepository orderRepository,
                                  LaptopModelRepository laptopModelRepository) {
        this.orderDetailRepository = orderDetailRepository;
        this.orderRepository = orderRepository;
        this.laptopModelRepository = laptopModelRepository;
    }

    @Transactional
    @Override
    public List<OrderDetailResponse> getAllOrderDetails() {
        return orderDetailRepository.findAll().stream()
                .map(OrderDetailMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public OrderDetailResponse getOrderDetailById(UUID id) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found"));

        return OrderDetailMapper.convertToResponse(orderDetail);
    }

    @Transactional
    @Override
    public OrderDetailResponse createOrderDetail(OrderDetailDTO orderDetailDTO) {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order  not found!"));

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

        return OrderDetailMapper.convertToResponse(orderDetailExisting);
    }

    @Transactional
    @Override
    public OrderDetailResponse updateOrderDetail(UUID id, OrderDetailDTO orderDetailDTO) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found!"));

        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new EntityNotFoundException("Order not found!"));

        LaptopModel laptopModel = laptopModelRepository.findById(orderDetailDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("LaptopModel not found!"));

        existingOrderDetail.setOrder(order);
        existingOrderDetail.setLaptopModel(laptopModel);
        existingOrderDetail.setQuantity(orderDetailDTO.getQuantity());
        existingOrderDetail.setPrice(orderDetailDTO.getPrice());

        OrderDetail orderDetailExisting = orderDetailRepository.save(existingOrderDetail);

        return OrderDetailMapper.convertToResponse(orderDetailExisting);
    }

    @Transactional
    @Override
    public OrderDetailResponse partialUpdateOrderDetail(UUID id, Map<String, Object> fieldsToUpdate) {
        OrderDetail orderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail with ID " + id + " not found!"));

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
        return OrderDetailMapper.convertToResponse(updatedOrderDetail);
    }

    @Transactional
    @Override
    public void deleteOrderDetail(UUID id) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("OrderDetail not found!"));

        orderDetailRepository.delete(existingOrderDetail);
    }


}