package com.example.demo.Service.Impl;

import com.example.demo.DTO.CustomerDTO;
import com.example.demo.Models.*;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.CustomerService;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository,AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
    }

    // 1. Lấy tất cả khách hàng
    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(customer -> CustomerDTO.builder()
                .customerId(customer.getCustomerId().getId()) // Trích xuất ID từ Account
                .addressIds(customer.getAddressList() != null
                        ? customer.getAddressList().stream().map(a -> a.getId()).collect(Collectors.toList())
                        : null)
                .paymentIds(customer.getPaymentList() != null
                        ? customer.getPaymentList().stream().map(p -> p.getId()).collect(Collectors.toList())
                        : null)
                .orderIds(customer.getOderList() != null
                        ? customer.getOderList().stream().map(o -> o.getId()).collect(Collectors.toList())
                        : null)
                .cartIds(customer.getCartList() != null
                        ? customer.getCartList().stream().map(c -> c.getId()).collect(Collectors.toList())
                        : null)
                .build()).collect(Collectors.toList());
    }

    // 2. Lấy khách hàng theo ID
    @Override
    public CustomerDTO getCustomerById(UUID id) {
        Account accountCustomer = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));
        Customer customer = customerRepository.findById(accountCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));

        return CustomerDTO.builder()
                .customerId(customer.getCustomerId().getId())
                .addressIds(customer.getAddressList() != null
                        ? customer.getAddressList().stream().map(Address::getId).collect(Collectors.toList())
                        : null)
                .paymentIds(customer.getPaymentList() != null
                        ? customer.getPaymentList().stream().map(Payment::getId).collect(Collectors.toList())
                        : null)
                .orderIds(customer.getOderList() != null
                        ? customer.getOderList().stream().map(Order::getId).collect(Collectors.toList())
                        : null)
                .cartIds(customer.getCartList() != null
                        ? customer.getCartList().stream().map(Cart::getId).collect(Collectors.toList())
                        : null)
                .build();
    }


    // 5. Xóa khách hàng
    @Override
    public void deleteCustomer(UUID id) {
        Account accountCustomer = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));
        Customer customer = customerRepository.findById(accountCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));
        customerRepository.delete(customer);
    }
}