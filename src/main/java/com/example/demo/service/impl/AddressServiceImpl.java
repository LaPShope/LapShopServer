package com.example.demo.service.impl;


import com.example.demo.dto.response.AddressResponse;
import com.example.demo.model.Address;
import com.example.demo.model.Customer;
import com.example.demo.repository.AddressRepository;
import com.example.demo.repository.CustomerRepository;
import com.example.demo.service.AddressService;
import com.example.demo.common.AuthUtil;
import com.example.demo.dto.AddressDTO;

import com.example.demo.mapper.AddressMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.model.Account;
import com.example.demo.repository.AccountRepository;

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final RedisService redisService;
    private final AccountRepository accountRepository;

    public AddressServiceImpl(AccountRepository accountRepository, RedisService redisService, AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.redisService = redisService;
        this.accountRepository = accountRepository;
    }

    // Lấy tất cả địa chỉ của một khách hàng
    @Override
    public List<AddressDTO> getAllAddress() {
        String currentUserEmail = AuthUtil.AuthCheck();

        List<AddressDTO> cachedAddress = redisService.getObject("allAddressCustomerId:" + currentUserEmail, new TypeReference<List<AddressDTO>>() {
        });

        if (cachedAddress != null && !cachedAddress.isEmpty()) {
            return cachedAddress;
        }

        Account customer = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        List<AddressDTO> addresses = customer.getCustomer().getAddressList().stream()
                .map(AddressMapper::convertToDTO)
                .toList();

        redisService.setObject("allAddressCustomerId:" + currentUserEmail, addresses, 600);

        return addresses;
    }

    // Lấy địa chỉ theo ID
    @Override
    public AddressResponse getAddressById(UUID id) {
        AddressResponse cachedAddress = redisService.getObject("addressId:" + id, new TypeReference<AddressResponse>() {
        });

        if (cachedAddress != null) {
            return cachedAddress;
        }

        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(address.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to view this address");
        }
        
        AddressResponse addressResponse = AddressMapper.convertToResponse(address);

        redisService.setObject("address:" + id, addressResponse, 600);

        return addressResponse;
    }

    // Tạo mới địa chỉ
    @Transactional
    @Override
    public AddressResponse createAddress(AddressDTO addressDTO) {
        // Customer customer = customerRepository.findById(addressDTO.getCustomerId())
        //         .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        // if (!currentUserEmail.equals(customer.getAccount().getEmail())) {
        //     throw new SecurityException("User is not authorized to create address");
        // }

        Account customer = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));


        Address address = Address.builder()
                .id(null)
                .customer(customer.getCustomer())
                .city(addressDTO.getCity())
                .district(addressDTO.getDistrict())
                .ward(addressDTO.getWard())
                .street(addressDTO.getStreet())
                .phone(addressDTO.getPhone())
                .build();

        customer.getCustomer().getAddressList().add(address);

        Address addressExisting = addressRepository.save(address);

        AddressResponse addressResponse = AddressMapper.convertToResponse(addressExisting);

        redisService.deleteByPatterns(List.of("*ustomer*", "allAddressCustomerId:" + currentUserEmail));

        redisService.setObject("address:" + addressExisting.getId(), addressResponse, 600);

        return addressResponse;
    }

    @Transactional
    @Override
    public AddressResponse updateAddress(UUID idToUpdate, AddressDTO updatedAddress) {

        Address addressToUpdate = addressRepository.findById(idToUpdate)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(addressToUpdate.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update address");
        }

        addressToUpdate.setCity(updatedAddress.getCity());
        addressToUpdate.setPhone(updatedAddress.getPhone());
        addressToUpdate.setStreet(updatedAddress.getStreet());
        addressToUpdate.setDistrict(updatedAddress.getDistrict());
        addressToUpdate.setWard(updatedAddress.getWard());


        Address addressExisting = addressRepository.save(addressToUpdate);

        AddressResponse addressResponse = AddressMapper.convertToResponse(addressExisting);

        redisService.deleteByPatterns(List.of("*ustomer*", "*ddress*"));

        redisService.setObject("address:" + addressResponse.getId(), addressResponse, 600);

        return addressResponse;
    }

    @Transactional
    @Override
    public AddressResponse partialUpdateAddress(UUID id, Map<String, Object> fieldsToUpdate) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address with ID " + id + " not found!"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(address.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update this account");
        }

        Class<?> clazz = address.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    field.set(address, newValue);
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Address updatedAddress = addressRepository.save(address);

        AddressResponse addressResponse = AddressMapper.convertToResponse(updatedAddress);

        redisService.deleteByPatterns(List.of("*ustomer*", "*ddress*"));

        redisService.setObject("addressId:" + id, addressResponse, 600);

        return addressResponse;
    }

    @Transactional
    @Override
    public void deleteAddress(UUID id) {
        Address addressExisting = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(addressExisting.getCustomer().getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to delete this address");
        }

        redisService.deleteByPatterns(List.of("*ustomer*"));

        addressRepository.deleteById(id);

    }


}

