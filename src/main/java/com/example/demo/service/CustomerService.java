package com.example.demo.service;

import com.example.demo.dto.response.CustomerResponse;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CustomerService {
    List<CustomerResponse> getAllCustomers(); // Lấy tất cả khách hàng
    CustomerResponse getCustomerById(UUID id); // Lấy chi tiết khách hàng theo ID
    CustomerResponse partialUpdateCustomer(UUID id, Map<String,Object> fieldsToUpdate);
    void deleteCustomer(UUID id); // Xóa 1 khách hàng
}