package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.common.Enums;
import com.example.demo.dto.PaymentMethodDTO;
import com.example.demo.model.PaymentMethod;
import com.example.demo.repository.PaymentMethodRepository;
import com.example.demo.service.PaymentMethodService;

import com.example.demo.mapper.PaymentMethodMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentMethodServiceImpl implements PaymentMethodService {
    private final RedisService redisService;
    private final PaymentMethodRepository paymentMethodRepository;

    public PaymentMethodServiceImpl(RedisService redisService, PaymentMethodRepository paymentMethodRepository) {
        this.paymentMethodRepository = paymentMethodRepository;
        this.redisService = redisService;
    }


    // Lấy danh sách tất cả PaymentMethod
    @Override
    public List<PaymentMethodDTO> getAllPaymentMethods() {
        List<PaymentMethodDTO> cachedPaymentMethods = redisService.getObject("allPaymentMethod", new TypeReference<List<PaymentMethodDTO>>() {});
        if(cachedPaymentMethods != null && !cachedPaymentMethods.isEmpty()){
            return cachedPaymentMethods;
        }

        List<PaymentMethodDTO> paymentMethodDTOs = paymentMethodRepository.findAll().stream()
                .map(paymentMethod -> {

                    Map<String, Object> dataMap = paymentMethod.getData();

                    return PaymentMethodDTO.builder()
                            .id(paymentMethod.getId())
                            .data(dataMap)
                            .type(paymentMethod.getType())
                            .build();
                })
                .collect(Collectors.toList());

        redisService.setObject("allPaymentMethod",paymentMethodDTOs,600);

        return paymentMethodDTOs;
    }

    // Lấy chi tiết PaymentMethod theo ID
    @Override
    public PaymentMethodDTO getPaymentMethodById(UUID id) {
        PaymentMethodDTO cachedPaymentMethods = redisService.getObject("allPaymentMethod", new TypeReference<PaymentMethodDTO>() {});
        if(cachedPaymentMethods != null){
            return cachedPaymentMethods;
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod with ID " + id + " not found!"));

        Map<String, Object> dataMap = paymentMethod.getData();

        PaymentMethodDTO paymentMethodDTO = PaymentMethodDTO.builder()
                .id(paymentMethod.getId())
                .data(dataMap)
                .type(paymentMethod.getType())
                .build();

        redisService.setObject("paymentMethod:"+id,paymentMethodDTO,600);

        return paymentMethodDTO;
    }


    // Tạo mới PaymentMethod
    @Transactional
    @Override
    public PaymentMethodDTO createPaymentMethod(PaymentMethodDTO paymentMethodDTO) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        PaymentMethod paymentMethod = PaymentMethod.builder()
                .id(null)
                .data(paymentMethodDTO.getData())
                .type(paymentMethodDTO.getType())
                .build();

        PaymentMethod paymentMethodExisting = paymentMethodRepository.save(paymentMethod);

        PaymentMethodDTO cachedPaymentMethod = PaymentMethodMapper.convertToDTO(paymentMethodExisting);

        redisService.deleteByPatterns(List.of("allPaymentMethod"));
        redisService.setObject("paymentMethod:"+cachedPaymentMethod.getId(),cachedPaymentMethod,600);

        return cachedPaymentMethod;
    }

    // Cập nhật PaymentMethod
    @Transactional
    @Override
    public PaymentMethodDTO updatePaymentMethod(UUID id, PaymentMethodDTO paymentMethodDTO) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        PaymentMethod existingPaymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod with ID " + id + " not found!"));

        // Cập nhật các thuộc tính của PaymentMethod
        existingPaymentMethod.setData(paymentMethodDTO.getData());
        existingPaymentMethod.setId(id);
        existingPaymentMethod.setType(Enums.PaymentType.valueOf(paymentMethodDTO.getType().name()));

        PaymentMethod paymentMethodExisting = paymentMethodRepository.save(existingPaymentMethod);
        PaymentMethodDTO cachedPaymentMethod = PaymentMethodMapper.convertToDTO(paymentMethodExisting);

        redisService.deleteByPatterns(List.of("allPaymentMethod","paymentMethod:"+id));
        redisService.setObject("paymentMethod:"+paymentMethodDTO.getId(),cachedPaymentMethod,600);

        return cachedPaymentMethod;
    }

    @Transactional
    @Override
    public PaymentMethodDTO partialUpdatePaymentMethod(UUID id, Map<String, Object> fieldsToUpdate) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("PaymentMethod with ID " + id + " not found!"));

        Class<?> clazz = paymentMethod.getClass();

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
                            field.set(paymentMethod, enumValue);
                        } catch (IllegalArgumentException e) {
                            throw new IllegalArgumentException("Invalid enum value '" + newValue + "' for field: " + fieldName);
                        }
                    } else {
                        field.set(paymentMethod, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        PaymentMethod updatedPaymentMethod = paymentMethodRepository.save(paymentMethod);
        PaymentMethodDTO cachedPaymentMethod = PaymentMethodMapper.convertToDTO(updatedPaymentMethod);

        redisService.deleteByPatterns(List.of("allPaymentMethod","paymentMethod:"+id));
        redisService.setObject("paymentMethod:"+id,cachedPaymentMethod,600);

        return cachedPaymentMethod;
    }


    @Transactional
    @Override
    public void deletePaymentMethod(UUID id) {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        PaymentMethod paymentMethod = paymentMethodRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PaymentMethod with ID " + id + " not found!"));
//        paymentMethod.getPaymentList().clear();

        redisService.deleteByPatterns(List.of("allPaymentMethod","paymentMethod:"+id));

        paymentMethodRepository.delete(paymentMethod);
    }


}