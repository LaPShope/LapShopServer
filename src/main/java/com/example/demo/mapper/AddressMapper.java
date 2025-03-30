package com.example.demo.mapper;

import com.example.demo.dto.AddressDTO;
import com.example.demo.dto.response.AddressResponse;
import com.example.demo.model.Address;

public class AddressMapper {
    public static AddressDTO convertToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .customerId(address.getCustomer().getAccount().getId())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .street(address.getStreet())
                .phone(address.getPhone())
                .build();
    }

    public static AddressResponse convertToResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .customer(CustomerMapper.convertToDTO(address.getCustomer()))
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .street(address.getStreet())
                .phone(address.getPhone())
                .build();
    }
}
