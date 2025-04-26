package com.example.demo.service.impl;

import com.example.demo.common.AuthUtil;
import com.example.demo.dto.CartDTO;
import com.example.demo.dto.response.cart.CartResponse;
import com.example.demo.model.*;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.repository.LaptopOnCartRepository;
import com.example.demo.service.CartService;
import com.example.demo.mapper.CartMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.transaction.annotation.Transactional;

import com.example.demo.repository.AccountRepository;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final LaptopOnCartRepository laptopOnCartRepository;
    private final RedisService redisService;
    private final AccountRepository accountRepository;

    public CartServiceImpl(AccountRepository accountRepository, RedisService redisService, CartRepository cartRepository, CustomerRepository customerRepository,LaptopOnCartRepository laptopOnCartRepository) {
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.laptopOnCartRepository=laptopOnCartRepository;
        this.redisService = redisService;
        this.accountRepository = accountRepository;
    }

    // Lấy tất cả Cart
    @Override
    public List<CartResponse> getAllCarts() {
        String currentUserEmail = AuthUtil.AuthCheck();

        List<CartResponse> cachedCarts = redisService.getObject("allCartByCustomerEmail"+currentUserEmail, new TypeReference<List<CartResponse>>() {});
        if (cachedCarts != null && !cachedCarts.isEmpty()) {
            return cachedCarts;
        }

        List<CartResponse> cartResponses = cartRepository.findAll().stream()
                .filter(cart -> cart.getCustomer().getAccount().getEmail().equals(currentUserEmail))
                .map(CartMapper::convertToResponse)
                .collect(Collectors.toList());

        redisService.setObject("allCartByCustomerEmail"+currentUserEmail,cartResponses,600);

        return cartResponses;
    }

    // Lấy Cart theo ID
    @Override
    public CartResponse getCartById(UUID id) {
        String currentUserEmail = AuthUtil.AuthCheck();

        CartResponse cachedCart = redisService.getObject("cart:"+id, new TypeReference<CartResponse>() {});
        if (cachedCart != null) {
            return cachedCart;
        }

        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart with ID " + id + " not found!"));

        if(!cart.getCustomer().getAccount().getEmail().equals(currentUserEmail)) {
            throw new SecurityException("User is not authorized to view this cart");
        }

        CartResponse cartResponse = CartMapper.convertToResponse(cart);

        redisService.setObject("cart:"+id,cartResponse,600);

        return cartResponse;
    }

    // Tạo mới Cart
    @Transactional
    @Override
    public CartResponse createCart(CartDTO cartDTO) {
        String currentUserEmail = AuthUtil.AuthCheck();
        // Customer customer = customerRepository.findById(cartDTO.getCustomerId())
        //         .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        Account customer = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        //kiem tra user qua email
        if(!currentUserEmail.equals(customer.getEmail())){
            throw new SecurityException("User is not authorized to create this cart ");
        }

        Cart cart = Cart.builder()
                .customer(customer.getCustomer())
                .build();
        Cart cartExisting = cartRepository.save(cart);

        CartResponse cachedCart = CartMapper.convertToResponse(cartExisting);

        redisService.deleteByPatterns(List.of("allCart"));
        redisService.setObject("cart:"+cachedCart.getId(),cachedCart,600);

        return cachedCart;
    }

    // Cập nhật Cart theo ID
    @Transactional
    @Override
    public CartResponse updateCart(UUID id, CartDTO cartDTO) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(cart.getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to update this cart");
        }
        // Customer customer = customerRepository.findById(cartDTO.getCustomerId())
        //         .orElseThrow(() -> new EntityNotFoundException("Customer not found!"));

        // cart.setCustomer(customer);
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

        CartResponse cartResponse = CartMapper.convertToResponse(cartExisting);

        redisService.deleteByPatterns(List.of("allCart","cart:"+id));
        redisService.setObject("cart:"+id,cartResponse,6000);

        return  cartResponse;
    }

    @Transactional
    @Override
    public CartResponse partialUpdateCart(UUID id, Map<String, Object> fieldsToUpdate) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cart with ID " + id + " not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(cart.getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to update this cart");
        }
    
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
        CartResponse cartResponse = CartMapper.convertToResponse(updatedCart);

        redisService.deleteByPatterns(List.of("allCart","cart:"+id));
        redisService.setObject("cart:"+id,cartResponse,600);

        return cartResponse;
    }


    @Transactional
    @Override
    public void deleteCart(UUID id) {
        Cart cart = cartRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart with ID " + id + " not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if(!currentUserEmail.equals(cart.getCustomer().getAccount().getEmail())){
            throw new SecurityException("User is not authorized to delete this cart");
        }
    
        cart.getLaptopOnCarts().removeIf(laptopOnCart -> true);

        redisService.deleteByPatterns(List.of("allCart","cart:"+id));

        cartRepository.delete(cart);
    }


}