package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.Response.CustomerResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CustomerService {
    List<CustomerResponse> getAllCustomers(); // Lấy tất cả khách hàng
    CustomerResponse getCustomerById(UUID id); // Lấy chi tiết khách hàng theo ID
    CustomerResponse partialUpdateCustomer(UUID id, Map<String,Object> fieldsToUpdate);
    void deleteCustomer(UUID id); // Xóa 1 khách hàng
}