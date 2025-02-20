package com.example.demo.Service.Impl;


import com.example.demo.Models.Address;
import com.example.demo.Models.Customer;
import com.example.demo.Repository.AddressRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.AddressService;
import com.example.demo.DTO.AddressDTO;

import com.example.demo.mapper.AddressMapper;
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

    public AddressServiceImpl(AddressRepository addressRepository, CustomerRepository customerRepository) {
        this.addressRepository = addressRepository;
        this.customerRepository = customerRepository;
    }

    // Lấy tất cả địa chỉ của một khách hàng
    @Transactional
    @Override
    public List<AddressDTO> getAllAddress(UUID customerId) {
        List<Address> addresses = addressRepository.findByCustomerId(customerId);
        return addresses.stream()
            .map(AddressMapper::convertToDTO)
            .collect(Collectors.toList());
    }

    // Lấy địa chỉ theo ID
    @Transactional
    @Override
    public AddressDTO getAddressById(UUID id) {
        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        return AddressMapper.convertToDTO(address);
    }
    
    // Tạo mới địa chỉ
    @Transactional
    @Override
    public AddressDTO createAddress(AddressDTO addressDTO) {
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

        return AddressMapper.convertToDTO(addressExisting);
    }
    
    // Cập nhật thông tin địa chỉ
    @Transactional
    @Override
    public AddressDTO updateAddress(UUID idToUpdate, AddressDTO updatedAddress) {
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

        return AddressMapper.convertToDTO(addressExisting);
    }

    public AddressDTO partialUpdateAddress(UUID id, Map<String, Object> fieldsToUpdate) {
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
        return AddressMapper.convertToDTO(updatedAddress);
    }

    // Xóa địa chỉ
    @Transactional
    @Override
    public void deleteAddress(UUID id) {
        Address addressExisting = addressRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Address not found"));
//        Customer customerExisting = addressExisting.getCustomer();
//
//        customerExisting.getAddressList().remove(addressExisting);

        addressRepository.deleteById(id);

    }


}

