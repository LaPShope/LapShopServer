package com.example.demo.Service.Impl;

import com.example.demo.Common.ConvertDate;
import com.example.demo.Common.ConvertSnakeToCamel;
import com.example.demo.DTO.AddressDTO;
import com.example.demo.DTO.CustomerDTO;
import com.example.demo.DTO.Response.CustomerResponse;
import com.example.demo.Models.*;
import com.example.demo.Repository.AccountRepository;
import com.example.demo.Repository.CustomerRepository;
import com.example.demo.Service.CustomerService;
import com.example.demo.mapper.AddressMapper;
import com.example.demo.mapper.CustomerMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.lang.ref.Reference;
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
    private final RedisService redisService;

    public CustomerServiceImpl(RedisService redisService, CustomerRepository customerRepository,AccountRepository accountRepository) {
        this.customerRepository = customerRepository;
        this.accountRepository = accountRepository;
        this.redisService = redisService;
    }

    // 1. Lấy tất cả khách hàng
    @Override
    public List<CustomerResponse> getAllCustomers() {
        List<CustomerResponse> cachedCustomers = redisService.getObject("allCustomer", new TypeReference<List<CustomerResponse>>() {});

        if(cachedCustomers != null && !cachedCustomers.isEmpty()){
            return cachedCustomers;
        }

        List<CustomerResponse> customerResponses = customerRepository.findAll().stream().map(CustomerMapper::convertToResponse).collect(Collectors.toList());

        redisService.setObject("allCustomer",customerResponses,600);

        return customerResponses;
    }

    // 2. Lấy khách hàng theo ID
    @Override
    public CustomerResponse getCustomerById(UUID id) {
        CustomerResponse cachedCustomer = redisService.getObject("customer:" + id, new TypeReference<CustomerResponse>() {});

        if(cachedCustomer!=null){
            return cachedCustomer;
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));

        CustomerResponse customerResponse = CustomerMapper.convertToResponse(customer);

        redisService.setObject("customer:"+id,customerResponse,600);

        return customerResponse;
    }

    @Override
    public CustomerResponse partialUpdateCustomer(UUID id, Map<String, Object> fieldsToUpdate) {
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

        redisService.del("allCustomer");
        redisService.del("customer:"+id);

        CustomerResponse cachedCustomer = CustomerMapper.convertToResponse(updatedCustomer);

        redisService.setObject("customer:"+id,cachedCustomer,600);

        return cachedCustomer;
    }

    // 5. Xóa khách hàng
    @Override
    public void deleteCustomer(UUID id) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer with ID " + id + " not found!"));

        redisService.del("customer:"+id);

        customerRepository.delete(customer);
    }
}