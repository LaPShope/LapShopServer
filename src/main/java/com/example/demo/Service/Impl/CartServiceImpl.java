package com.example.demo.Service.Impl;

import com.example.demo.DTO.CartDTO;
import com.example.demo.DTO.Response.CartResponse.CartResponse;
import com.example.demo.Models.*;
import com.example.demo.Repository.CartRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Repository.LaptopOnCartRepository;
import com.example.demo.Service.CartService;
import com.example.demo.mapper.CartMapper;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final LaptopOnCartRepository laptopOnCartRepository;

    public CartServiceImpl(CartRepository cartRepository, CustomerRepository customerRepository,LaptopOnCartRepository laptopOnCartRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.laptopOnCartRepository=laptopOnCartRepository;
    }

    // Lấy tất cả Cart
    @Override
    public List<CartResponse> getAllCarts() {

        return cartRepository.findAll().stream()
                .map(CartMapper::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy Cart theo ID
    @Override
    public CartResponse getCartById(UUID id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart with ID " + id + " not found!"));

        return CartMapper.convertToResponse(cart);
    }

    // Tạo mới Cart
    @Override
    public CartResponse createCart(CartDTO cartDTO) {
        Customer customer = customerRepository.findById(cartDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Cart cart = Cart.builder()
                .customer(customer)
                .build();
        Cart cartExisting = cartRepository.save(cart);

        return CartMapper.convertToResponse(cartExisting);
    }

    // Cập nhật Cart theo ID
    @Override
    public CartResponse updateCart(UUID id, CartDTO cartDTO) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found!"));

        Customer customer = customerRepository.findById(cartDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        cart.setCustomer(customer);
        //loai bo toan bo laptopOnCart
        cart.getLaptopOnCarts().removeIf(laptop -> true);
        //lay laptopOnCart moi
        List<LaptopOnCart> laptopOnCarts = Optional.ofNullable(cartDTO.getLaptopOnCartIds())
                .orElse(Collections.emptyList())
                .stream()
                .map(laptopOnCartId -> laptopOnCartRepository.findById(laptopOnCartId)
                        .orElseThrow(() -> new EntityNotFoundException("LaptopOnCart not found ")))
                .toList();

        cart.getLaptopOnCarts().addAll(laptopOnCarts);

        Cart cartExisting = cartRepository.save(cart);
        return CartMapper.convertToResponse(cartExisting);
    }

    public CartResponse partialUpdateCart(UUID id, Map<String, Object> fieldsToUpdate) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart with ID " + id + " not found!"));

        Class<?> clazz = cart.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    field.set(cart, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Cart updatedCart = cartRepository.save(cart);
        return CartMapper.convertToResponse(updatedCart);
    }


    // Xóa Cart theo ID xoa luon cac LaptopOnCart con
    @Override
    public void deleteCart(UUID id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart with ID " + id + " not found!"));

        cart.getLaptopOnCarts().removeIf(laptop -> true);

        cartRepository.delete(cart);
    }


}