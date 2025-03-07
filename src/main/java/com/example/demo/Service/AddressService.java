package com.example.demo.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.example.demo.DTO.AccountDTO;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.Response.AddressResponse;

public interface AddressService {
    public AddressResponse partialUpdateAddress(UUID id, Map<String,Object> fieldsToUpdate );
    public List<AddressResponse> getAllAddress(UUID customerId);
    public AddressResponse getAddressById(UUID id);
    public AddressResponse createAddress(AddressDTO addressDTO);
    public AddressResponse updateAddress(UUID idToUpdate, AddressDTO updatedAddressDTO);
    public void deleteAddress(UUID id);
}
