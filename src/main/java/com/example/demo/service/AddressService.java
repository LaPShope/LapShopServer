package com.example.demo.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.dto.AddressDTO;
import com.example.demo.dto.response.AddressResponse;

public interface AddressService {
    public AddressResponse partialUpdateAddress(UUID id, Map<String,Object> fieldsToUpdate );
    public List<AddressDTO> getAllAddress();
    public AddressResponse getAddressById(UUID id);
    public AddressResponse createAddress(AddressDTO addressDTO);
    public AddressResponse updateAddress(UUID idToUpdate, AddressDTO updatedAddressDTO);
    public void deleteAddress(UUID id);
}
