package com.example.demo.Service.Impl;

import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.ConvertSnakeToCamel;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.Models.*;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.CustomerService;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.mapper.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
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
        return customerRepository.findAll().stream().map(CustomerMapper::convertToDTO).collect(Collectors.toList());
    }

    // 2. Lấy khách hàng theo ID
    @Override
    public CustomerDTO getCustomerById(UUID id) {
        Account accountCustomer = accountRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));
        Customer customer = customerRepository.findById(accountCustomer.getId())
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));

        return CustomerMapper.convertToDTO(customer);
    }

    @Override
    public CustomerDTO partialUpdateCustomer(UUID id, Map<String, Object> fieldsToUpdate) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer with ID " + id + " not found!"));

        Class<?> clazz = customer.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            if(fieldName.equals("born_date")){
                fieldName= ConvertSnakeToCamel.snakeToCamel(fieldName);
            }
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {
                    if(field.getType().equals(Date.class)){
                        Date parsedDate = ConvertDate.convertToDate(newValue);
                        field.set(customer, parsedDate);
                    }
                    else{
                        field.set(customer, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Customer updatedCustomer = customerRepository.save(customer);
        return CustomerMapper.convertToDTO(updatedCustomer);
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