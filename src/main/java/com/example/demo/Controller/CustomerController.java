package com.example.demo.Controller;

import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.LaptopDTO;
import com.example.demo.Service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
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

        return ResponseEntity.ok(DataResponse.<List<CustomerDTO>>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(customerService.getAllCustomers())
                .build());
    }

    // 2. Lấy khách hàng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(@PathVariable UUID id) {

        return ResponseEntity.ok(DataResponse.<CustomerDTO>builder()
                .success(true)
                .message("Customer retrieved successfully")
                .data(customerService.getCustomerById(id))
                .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateLaptop(@PathVariable UUID id, @RequestBody Map<String, Object> fieldsToUpdate) {
        return ResponseEntity.ok(DataResponse.<CustomerDTO>builder()
                .success(true)
                .message("Laptop updated successfully")
                .data(customerService.partialUpdateCustomer(id, fieldsToUpdate))
                .build());
    }

    // 3. Xóa một khách hàng
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCustomer(@PathVariable UUID id) {
            customerService.deleteCustomer(id);
        return ResponseEntity.ok(DataResponse.builder()
                .success(true)
                .message("Customer deleted successfully")
                .build());

    }
}