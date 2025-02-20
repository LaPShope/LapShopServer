package com.example.demo.mapper;

import com.example.demo.DTO.AddressDTO;
import com.example.demo.Models.Address;

public class AddressMapper {
    public static AddressDTO convertToDTO(Address address) {
        return AddressDTO.builder()
                .id(address.getId())
                .customerId(address.getCustomer().getId())
                .city(address.getCity())
                .district(address.getDistrict())
                .ward(address.getWard())
                .street(address.getStreet())
                .phone(address.getPhone())
                .build();
    }
}
