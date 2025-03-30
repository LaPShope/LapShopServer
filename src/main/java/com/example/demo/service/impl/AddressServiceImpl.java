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

@Service
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final RedisService redisService;

    public AddressServiceImpl(RedisService redisService, AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.redisService = redisService;
    }

    // Lấy tất cả địa chỉ của một khách hàng
    @Override
    public List<AddressResponse> getAllAddress(UUID customerId) {
        List<AddressResponse> cachedAddress = redisService.getObject("allAddressCustomerId:" + customerId, new TypeReference<List<AddressResponse>>() {
        });

        if (cachedAddress != null && !cachedAddress.isEmpty()) {
            return cachedAddress;
        }

        List<AddressResponse> addresses = addressRepository.findByCustomerId(customerId).stream()
                .map(AddressMapper::convertToResponse)
                .toList();

        redisService.setObject("allAddressCustomerId:" + customerId, addresses, 600);

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
        AddressResponse addressResponse = AddressMapper.convertToResponse(address);

        redisService.setObject("address:" + id, addressResponse, 600);

        return addressResponse;
    }

    // Tạo mới địa chỉ
    @Transactional
    @Override
    public AddressResponse createAddress(AddressDTO addressDTO) {
        Customer customer = customerRepository.findById(addressDTO.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(customer.getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to create address");
        }

        Address address = Address.builder()
                .id(null)
                .customer(customer)
                .city(addressDTO.getCity())
                .district(addressDTO.getDistrict())
                .ward(addressDTO.getWard())
                .street(addressDTO.getStreet())
                .phone(addressDTO.getPhone())
                .build();

        customer.getAddressList().add(address);

        Address addressExisting = addressRepository.save(address);

        AddressResponse addressResponse = AddressMapper.convertToResponse(addressExisting);

        redisService.deleteByPatterns(List.of("*ustomer:" + addressDTO.getCustomerId() + "*", "allAddressCustomerId:" + addressDTO.getCustomerId()));

        redisService.setObject("address:" + addressExisting.getId(), addressResponse, 600);

        return addressResponse;
    }

    // Cập nhật thông tin địa chỉ
    @Transactional
    @Override
    public AddressResponse updateAddress(UUID idToUpdate, AddressDTO updatedAddress) {
        Customer customer = customerRepository.findById(updatedAddress.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        //kiem tra user qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(customer.getAccount().getEmail())) {
            throw new SecurityException("User is not authorized to update address");
        }

        Address addressToUpdate = addressRepository.findById(idToUpdate)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        addressToUpdate.setId(customer.getAccount().getId());
        addressToUpdate.setCity(updatedAddress.getCity());
        addressToUpdate.setPhone(updatedAddress.getPhone());
        addressToUpdate.setStreet(updatedAddress.getStreet());
        addressToUpdate.setDistrict(updatedAddress.getDistrict());
        addressToUpdate.setWard(updatedAddress.getWard());

        addressToUpdate.setId(idToUpdate);

        Address addressExisting = addressRepository.save(addressToUpdate);

        AddressResponse addressResponse = AddressMapper.convertToResponse(addressExisting);

        redisService.deleteByPatterns(List.of("*ustomer:" + updatedAddress.getCustomerId() + "*", "*ddress:" + idToUpdate + "*"));

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
            throw new SecurityException("User is not authorized to delete this account");
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

        redisService.deleteByPatterns(List.of("*ustomer:" + address.getCustomer().getAccount() + "*", "*ddress:" + id + "*"));

        redisService.setObject("addressId:" + id, addressResponse, 600);

        return addressResponse;
    }

    // Xóa địa chỉ
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

        redisService.deleteByPatterns(List.of("*ustomer:" + addressExisting.getCustomer().getAccount() + "*"));

        addressRepository.deleteById(id);

    }


}

