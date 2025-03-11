package com.example.demo.Service.Impl;


import com.example.demo.DTO.Response.AddressResponse;
import com.example.demo.Models.Address;
import com.example.demo.Models.Customer;
import com.example.demo.Repository.AddressRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.AddressService;
import com.example.demo.DTO.AddressDTO;

import com.example.demo.mapper.AddressMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AddressServiceImpl implements AddressService{

    private final AddressRepository addressRepository;
    private final CustomerRepository customerRepository;
    private final RedisService redisService;

    public AddressServiceImpl(RedisService redisService, AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
        this.redisService =redisService;
    }

    // Lấy tất cả địa chỉ của một khách hàng
    @Transactional
    @Override
    public List<AddressResponse> getAllAddress(UUID customerId) {
        List<AddressResponse> cachedAddress = redisService.getObject("allAddressCustomerId:" + customerId, new TypeReference<List<AddressResponse>>() {});

        if(cachedAddress != null && !cachedAddress.isEmpty()){
            return cachedAddress;
        }

        List<AddressResponse> addresses = addressRepository.findByCustomerId(customerId).stream()
            .map(AddressMapper::convertToResponse)
            .toList();

        redisService.setObject("allAddressCustomerId:"+customerId,addresses,600);

        return addresses;
    }

    // Lấy địa chỉ theo ID
    @Transactional
    @Override
    public AddressResponse getAddressById(UUID id) {
        AddressResponse cachedAddress = redisService.getObject("addressId:" + id, new TypeReference<AddressResponse>() {});

        if(cachedAddress != null){
            return cachedAddress;
        }

        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        AddressResponse addressResponse = AddressMapper.convertToResponse(address);

        redisService.setObject("addressId:"+id,addressResponse,600);

        return addressResponse;
    }
    
    // Tạo mới địa chỉ
    @Transactional
    @Override
    public AddressResponse createAddress(AddressDTO addressDTO) {
        Customer  customer = customerRepository.findById(addressDTO.getCustomerId())
                           .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

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

        redisService.deleteByPattern("*customer*");

        redisService.setObject("addressId:"+addressExisting.getId(),addressResponse,600);

        return addressResponse;
    }
    
    // Cập nhật thông tin địa chỉ
    @Transactional
    @Override
    public AddressResponse updateAddress(UUID idToUpdate, AddressDTO updatedAddress) {
        Customer customer = customerRepository.findById(updatedAddress.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        Address addressToUpdate = addressRepository.findById(idToUpdate)
                .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        addressToUpdate.setId(customer.getId());
        addressToUpdate.setCity(updatedAddress.getCity());
        addressToUpdate.setPhone(updatedAddress.getPhone());
        addressToUpdate.setStreet(updatedAddress.getStreet());
        addressToUpdate.setDistrict(updatedAddress.getDistrict());
        addressToUpdate.setWard(updatedAddress.getWard());

        addressToUpdate.setId(idToUpdate);

        Address addressExisting = addressRepository.save(addressToUpdate);

        AddressResponse addressResponse = AddressMapper.convertToResponse(addressExisting);

        redisService.deleteByPattern("*ustomer:"+idToUpdate+"*");

        redisService.setObject("addressId:"+addressResponse.getId(), addressResponse,600);

        return addressResponse;
    }

    @Override
    public AddressResponse partialUpdateAddress(UUID id, Map<String, Object> fieldsToUpdate) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Address with ID " + id + " not found!"));

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

        redisService.deleteByPattern("*ustomer:"+address.getCustomer().getId()+"*");

        redisService.setObject("addressId:"+id,addressResponse,600);

        return addressResponse;
    }

    // Xóa địa chỉ
    @Transactional
    @Override
    public void deleteAddress(UUID id) {
        Address addressExisting = addressRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Address not found"));

        redisService.deleteByPattern("*ustomer:"+addressExisting.getCustomer().getId()+"*");

        addressRepository.deleteById(id);

    }


}

