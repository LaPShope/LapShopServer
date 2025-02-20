package com.example.demo.Service;

import com.example.demo.DTO.CustomerDTO;

import java.util.List;
import java.util.UUID;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers(); // Lấy tất cả khách hàng
    CustomerDTO getCustomerById(UUID id); // Lấy chi tiết khách hàng theo ID

    void deleteCustomer(UUID id); // Xóa 1 khách hàng
}