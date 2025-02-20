package com.example.demo.Service.Impl;

import com.example.demo.DTO.LaptopOnCartDTO;
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
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class LaptopOnCartServiceImpl implements LaptopOnCartService {

    private final LaptopOnCartRepository laptopOnCartRepository;
    private final CartRepository cartRepository;
    private final LaptopModelRepository laptopModelRepository;

    public LaptopOnCartServiceImpl(
            LaptopOnCartRepository laptopOnCartRepository,
            CartRepository cartRepository,
            LaptopModelRepository laptopModelRepository) {
        this.laptopOnCartRepository = laptopOnCartRepository;
        this.cartRepository = cartRepository;
        this.laptopModelRepository = laptopModelRepository;
    }

    // 1. Lấy tất cả LaptopOnCart
    @Override
    public List<LaptopOnCartDTO> getAllLaptopOnCarts() {
        return laptopOnCartRepository.findAll().stream()
                .map(LaptopOnCartMapper::convertToDTO)
                .collect(Collectors.toList());
    }

    // 2. Lấy LaptopOnCart theo ID
    @Override
    public LaptopOnCartDTO getLaptopOnCartById(UUID id) {
        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("LaptopOnCart with ID " + id + " not found!"));

        return LaptopOnCartMapper.convertToDTO(laptopOnCart);
    }

    // 3. Tạo mới LaptopOnCart
    @Override
    public LaptopOnCartDTO createLaptopOnCart(LaptopOnCartDTO laptopOnCartDTO) {
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
        return LaptopOnCartMapper.convertToDTO(laptopOnCartExisting);
    }

    // 4. Cập nhật LaptopOnCart
    @Override
    public LaptopOnCartDTO updateLaptopOnCart(UUID id, LaptopOnCartDTO laptopOnCartDTO) {
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

        return LaptopOnCartMapper.convertToDTO(laptopOnCartExisting);
    }

    @Override
    public LaptopOnCartDTO partialUpdateLaptopOnCart(UUID id, Map<String, Object> fieldsToUpdate) {
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
                    field.set(laptopOnCart, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        LaptopOnCart updatedLaptopOnCart = laptopOnCartRepository.save(laptopOnCart);
        return LaptopOnCartMapper.convertToDTO(updatedLaptopOnCart);
    }

    // 5. Xóa LaptopOnCart
    @Override
    public void deleteLaptopOnCart(UUID id) {
        LaptopOnCart laptopOnCart = laptopOnCartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("LaptopOnCart with ID " + id + " not found!"));

        laptopOnCartRepository.delete(laptopOnCart);
    }


}