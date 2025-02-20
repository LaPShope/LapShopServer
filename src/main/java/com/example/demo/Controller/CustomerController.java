package com.example.demo.Controller;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.Service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // 1. Lấy tất cả khách hàng
    @GetMapping
    public ResponseEntity<?> getAllCustomers() {

            List<CustomerDTO> customers = customerService.getAllCustomers();
            return ResponseEntity.ok(customers);
    }

    // 2. Lấy khách hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {

            CustomerDTO customer = customerService.getCustomerById(id);
            return ResponseEntity.ok(customer);
    }

    // 3. Xóa một khách hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {

            customerService.deleteCustomer(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }
}