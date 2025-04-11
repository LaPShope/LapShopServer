package com.example.demo.service;

import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.response.AdminResponse;

public interface AdminService {
    public AdminResponse createAdmin (AccountDTO accountDTO);
}
