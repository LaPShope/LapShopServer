package com.example.demo.Service.Impl;

import com.example.demo.DTO.LaptopOnCartDTO;
import com.example.demo.DTO.Response.LaptopOnCartResponse;
import com.example.demo.DTO.SaleDTO;
import com.example.demo.Models.Cart;
import com.example.demo.Models.LaptopModel;
import com.example.demo.Models.LaptopOnCart;
import com.example.demo.Models.Sale;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.LaptopModelRepository;
import com.example.demo.Repository.LaptopOnCartRepository;
import com.example.demo.Service.LaptopOnCartService;
import com.example.demo.mapper.LaptopOnCartMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LaptopOnCartServiceImpl implements LaptopOnCartService {

    private final RedisService redisService;
    private final LaptopOnCartRepository laptopOnCartRepository;
    private final CartRepository cartRepository;
    private final LaptopModelRepository laptopModelRepository;

    public LaptopOnCartServiceImpl(
            RedisService redisService,
            LaptopOnCartRepository laptopOnCartRepository,
            CartRepository cartRepository,
            LaptopModelRepository laptopModelRepository) {
        this.laptopOnCartRepository = laptopOnCartRepository;
        this.cartRepository = cartRepository;
        this.laptopModelRepository = laptopModelRepository;
        this.redisService = redisService;
    }

    // 1. Lấy tất cả LaptopOnCart
    @Override
    public List<LaptopOnCartResponse> getAllLaptopOnCarts() {
        List<LaptopOnCartResponse> cachedLaptopOnCart = redisService.getObject("allLaptopOnCart", new TypeReference<List<LaptopOnCartResponse>>(){});

        if(cachedLaptopOnCart != null && !cachedLaptopOnCart.isEmpty()){
            return cachedLaptopOnCart;
        }

        List<LaptopOnCartResponse> laptopOnCartResponses = laptopOnCartRepository.findAll().stream()
                .map(LaptopOnCartMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allLaptopOnCart",laptopOnCartResponses,600);

        return laptopOnCartResponses;
    }

    // 2. Lấy LaptopOnCart theo ID
    @Override
    public LaptopOnCartResponse getLaptopOnCartById(UUID id) {
        LaptopOnCartResponse cachedLaptopOnCart = redisService.getObject("allLaptopOnCart", new TypeReference<LaptopOnCartResponse>(){});

        if(cachedLaptopOnCart != null){
            return cachedLaptopOnCart;
        }
        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LaptopOnCart with ID " + id + " not found!"));

        LaptopOnCartResponse laptopOnCartResponse = LaptopOnCartMapper.convertToResponse(laptopOnCart);

        redisService.setObject("laptopOnCart:"+id,laptopOnCartResponse,600);

        return  laptopOnCartResponse;
    }

    // 3. Tạo mới LaptopOnCart
    @Override
    public LaptopOnCartResponse createLaptopOnCart(LaptopOnCartDTO laptopOnCartDTO) {
        Cart cart = cartRepository.findById(laptopOnCartDTO.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart not found!"));

        LaptopModel laptopModel = laptopModelRepository.findById(laptopOnCartDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model not found!"));

        LaptopOnCart laptopOnCart = LaptopOnCart.builder()
                .cart(cart)
                .laptopModel(laptopModel)
                .quantity(laptopOnCartDTO.getQuantity())
                .build();

        LaptopOnCart laptopOnCartExisting = laptopOnCartRepository.save(laptopOnCart);

        LaptopOnCartResponse cachedLaptopOnCart = LaptopOnCartMapper.convertToResponse(laptopOnCartExisting);

        redisService.deleteByPatterns(List.of("*art:"+laptopOnCart.getCart().getId()+"*","allLaptopOnCart"));
        redisService.setObject("laptopOnCartId:",cachedLaptopOnCart,600);

        return cachedLaptopOnCart;
    }

    // 4. Cập nhật LaptopOnCart
    @Override
    public LaptopOnCartResponse updateLaptopOnCart(UUID id, LaptopOnCartDTO laptopOnCartDTO) {
        LaptopOnCartResponse cachLaptopOnCartResponse = redisService.getObject("laptopOnCart:" + id, new TypeReference<LaptopOnCartResponse>() {});
        if(cachLaptopOnCartResponse != null){
            return cachLaptopOnCartResponse;
        }

        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LaptopOnCart with ID " + id + " not found!"));

        Cart cart = cartRepository.findById(laptopOnCartDTO.getCartId())
                .orElseThrow(() -> new EntityNotFoundException("Cart with ID " + laptopOnCartDTO.getCartId() + " not found!"));

        LaptopModel laptopModel = laptopModelRepository.findById(laptopOnCartDTO.getLaptopModelId())
                .orElseThrow(() -> new EntityNotFoundException("Laptop Model with ID " + laptopOnCartDTO.getLaptopModelId() + " not found!"));

        laptopOnCart.setCart(cart);
        laptopOnCart.setLaptopModel(laptopModel);
        laptopOnCart.setQuantity(laptopOnCartDTO.getQuantity());

        LaptopOnCart laptopOnCartExisting = laptopOnCartRepository.save(laptopOnCart);

        LaptopOnCartResponse laptopOnCartResponse = LaptopOnCartMapper.convertToResponse(laptopOnCartExisting);

        redisService.deleteByPatterns(List.of("*art:"+id+"*","allLaptopOnCart"));
        redisService.setObject("laptopOnCart:"+id,laptopOnCartResponse,600);

        return laptopOnCartResponse;
    }

    @Override
    public LaptopOnCartResponse partialUpdateLaptopOnCart(UUID id, Map<String, Object> fieldsToUpdate) {
        LaptopOnCartResponse cachLaptopOnCartResponse = redisService.getObject("laptopOnCart:" + id, new TypeReference<LaptopOnCartResponse>() {});
        if(cachLaptopOnCartResponse != null){
            return cachLaptopOnCartResponse;
        }

        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LaptopOnCart with ID " + id + " not found!"));

        Class<?> clazz = laptopOnCart.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if (field.getType().equals(Integer.class)) {
                        field.set(laptopOnCart, Integer.parseInt(newValue.toString()));
                    }
                    field.set(laptopOnCart, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        LaptopOnCart updatedLaptopOnCart = laptopOnCartRepository.save(laptopOnCart);
        LaptopOnCartResponse laptopOnCartResponse = LaptopOnCartMapper.convertToResponse(updatedLaptopOnCart);

        redisService.deleteByPatterns(List.of("*art:"+id+"*","allLaptopOnCart"));
        redisService.setObject("laptopOnCart:"+id,laptopOnCartResponse,600);

        return laptopOnCartResponse;
    }

    // 5. Xóa LaptopOnCart
    @Override
    public void deleteLaptopOnCart(UUID id) {
        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LaptopOnCart with ID " + id + " not found!"));

        redisService.deleteByPatterns(List.of("*art:"+id+"*","allLaptopOnCart"));

        laptopOnCartRepository.delete(laptopOnCart);
    }


}