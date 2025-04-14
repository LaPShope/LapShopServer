package com.example.demo.service.impl;


import com.example.demo.common.AuthUtil;
import com.example.demo.common.Enums;
import com.example.demo.configuration.JwtService;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.request.auth.ChangePasswordRequest;
import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.request.auth.ResetPasswordRequest;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.auth.ChangePasswordResponse;
import com.example.demo.dto.response.auth.RegisterReponse;
import com.example.demo.dto.response.auth.ResetPasswordReponse;
import com.example.demo.model.Account;
import com.example.demo.model.Admin;
import com.example.demo.model.Customer;
import com.example.demo.repository.AccountRepository;
import com.example.demo.repository.AdminRepository;
import com.example.demo.service.AccountService;


import com.example.demo.mapper.AccountMapper;
import com.example.demo.service.EmailService;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.mail.MessagingException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {
    private final RedisService redisService;
    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    public AccountServiceImpl(
            JwtService jwtService,
            AdminRepository adminRepository,
            RedisService redisService,
            AccountRepository accountRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.redisService = redisService;
        this.adminRepository = adminRepository;
        this.jwtService = jwtService;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    private boolean existsAccountByEmail(String email) {
        return accountRepository.existsAccountByEmail(email) > 0;
    }

    //test lay token
    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Account account = accountRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!new BCryptPasswordEncoder().matches(loginRequest.getPassword(), account.getPassword())) {
            throw new SecurityException("Invalid password");
        }

        String token = jwtService.generateToken(
                account.getEmail(),
                account.getRole()
        );

        return LoginResponse.builder()
                .token(token)
                .email(account.getEmail())
                .role(account.getRole())
                .build();
    }

    @Override
    @Transactional
    public RegisterReponse register(RegisterRequest registerRequest) {
        if (this.existsAccountByEmail(registerRequest.getEmail())) {
            throw new EntityExistsException("Email already exists!");
        }

        Account account = Account.builder()
                .email(registerRequest.getEmail())
                .name(registerRequest.getName())
                .password(registerRequest.getPassword())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .role(Enums.Role.Customer)
                .build();

        Customer customer = new Customer();
        account.setCustomer(customer);
        customer.setAccount(account);

        Account accountExisting = accountRepository.save(account);

        RegisterReponse registerReponse = RegisterReponse.builder()
                .id(accountExisting.getId())
                .email(accountExisting.getEmail())
                .name(accountExisting.getName())
                .password(accountExisting.getPassword())
                .role(accountExisting.getRole())
                .token(jwtService.generateToken(accountExisting.getEmail(), Enums.Role.Customer))
                .build();
        redisService.setObject("account:" + accountExisting.getId(), registerReponse, 600);

        return registerReponse;
    }


    @Override
    public void forgotPassword(String email) {
        Long res = accountRepository.existsAccountByEmail(email);

        if (res == 0) {
            throw new EntityNotFoundException("Email not found");
        }

        String baseClientUrl = "http://localhost:3000/reset-password?token=";
        String token = UUID.randomUUID().toString();

        redisService.set(token, email, 60 * 3);

        try {
            this.emailService.sendLinkEmail(email, baseClientUrl + token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }

    @Override
    public ResetPasswordReponse changePassword(ResetPasswordRequest resetPasswordRequest) {

        String email = null;
        try {
            email = redisService.get(resetPasswordRequest.getToken());
        } catch (Exception e) {
            // do nothing
        }

        if (email == null || email.isEmpty()) {
            throw new EntityNotFoundException("Token not found");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        redisService.del(resetPasswordRequest.getToken());

        if (resetPasswordRequest.getNewPassword() != null && !resetPasswordRequest.getNewPassword().isEmpty()) {
            account.setPassword(passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
            accountRepository.save(account);
        }

        return ResetPasswordReponse.builder()
                .email(account.getEmail())
                .jwtToken(jwtService.generateToken(account.getEmail(), account.getRole()))
                .build();
    }

    @Override
    public ChangePasswordResponse changePassword(ChangePasswordRequest changePasswordRequest) {
        String currentUserEmail = AuthUtil.AuthCheck();
        Account account = accountRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), account.getPassword())) {
            throw new SecurityException("Invalid password");
        }

        if (changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isEmpty()) {
            throw new EntityNotFoundException("New password is required");
        }

        account.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        accountRepository.save(account);

        return ChangePasswordResponse.builder()
                .email(account.getEmail())
                .token(jwtService.generateToken(account.getEmail(), account.getRole()))
                .build();
    }

    // Lấy danh sách tài khoản
    @Override
    public List<AccountResponse> getAllAccounts() {
        if(!AuthUtil.isAdmin()){
            throw new SecurityException("User is not an Admin");
        }

        // kiem tra trong redis
        List<AccountResponse> cachedAccounts = redisService.getObject("allAccount", new TypeReference<List<AccountResponse>>() {
        });

        if (cachedAccounts != null && !cachedAccounts.isEmpty()) {
            return cachedAccounts;
        }

        List<AccountResponse> accounts = accountRepository.findAll().stream()
                .map(AccountMapper::convertToResponse)
                .collect(Collectors.toList());

        //Lưu vào Redis
        redisService.setObject("allAccount", accounts, 600);

        return accounts;
    }

    // Lấy chi tiết một tài khoản
    @Override
    public AccountResponse getAccount(UUID id) {
        AccountResponse cachedAccount = redisService.getObject("account:" + id, new TypeReference<AccountResponse>() {});

        if (cachedAccount != null) {
            return cachedAccount;
        }
        Account accountExisting = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
        AccountResponse account = AccountMapper.convertToResponse(accountExisting);

        redisService.setObject("account:" + id, account, 600);

        return account;
    }

    @Override
    public Account getAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));
    }

    // Tạo mới tài khoản
    @Transactional
    @Override
    public AccountResponse createAccount(AccountDTO accountDTO) {
        if (accountRepository.findByEmail(accountDTO.getEmail()).isPresent()) {
            throw new EntityExistsException("Email already exists!");
        }
        Account account = Account.builder()
                .id(null)
                .email(accountDTO.getEmail())
                .name(accountDTO.getName())
                .password(accountDTO.getPassword())
                .role(accountDTO.getRole())
                .build();


        Customer customer = new Customer();
        customer.setAccount(account);
//        account.setCustomerId(customer);

        Account accountExisting = accountRepository.save(account);

        AccountResponse cachedAccount = AccountMapper.convertToResponse(accountExisting);

        redisService.setObject("account:" + accountExisting, cachedAccount, 600);

        redisService.del("allAccount");

        return cachedAccount;
    }


    // Cập nhật thông tin tài khoản
    @Transactional
    @Override
    public AccountResponse updateAccount(UUID id, AccountDTO updatedAccount) {
        Account existingAccount = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(existingAccount.getEmail())) {
            throw new SecurityException("User is not authorized to update this account");
        }

        Optional<Account> existingAccount1 = accountRepository.findByEmail(updatedAccount.getEmail());

        if (existingAccount1.isPresent()) {
            if (!existingAccount1.get().getEmail().equals(existingAccount.getEmail())) {
                throw new EntityExistsException("Email already existed");
            }
        }

        existingAccount.setName(updatedAccount.getName());
        existingAccount.setEmail(updatedAccount.getEmail());
        // existingAccount.setRole(updatedAccount.getRole());
        existingAccount.setPassword(updatedAccount.getPassword());

        Account account = accountRepository.save(existingAccount);

        AccountResponse cachedAccount = AccountMapper.convertToResponse(account);

        redisService.del("allAccount");
        redisService.del("account:" + account.getId());

        redisService.setObject("account:" + account.getId(), cachedAccount, 600);

        return cachedAccount;
    }

    @Transactional
    @Override
    public AccountResponse partialUpdateAccount(UUID id, Map<String, Object> fieldsToUpdate) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account with ID " + id + " not found!"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(account.getEmail())) {
            throw new SecurityException("User is not authorized to update this account");
        }

        Class<?> clazz = account.getClass();

        for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
            String fieldName = entry.getKey();
            Object newValue = entry.getValue();

            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);

                if (newValue != null) {

                    if (field.getType().isEnum()) {
                        // try {
                        //     Object enumValue = Enum.valueOf((Class<Enum>) field.getType(), newValue.toString());
                        //     field.set(account, enumValue);
                        //     if (enumValue.equals(Enums.Role.Admin)) {
                        //         Admin admin = Admin.builder()

                        //                 .build();
                        //         adminRepository.save(admin);
                        //     } else if (enumValue.equals(Enums.Role.Customer)) {

                        //         adminRepository.deleteById(account.getId());
                        //     }
                        // } catch (IllegalArgumentException e) {
                        //     throw new IllegalArgumentException("Invalid enum value for field: " + fieldName);
                        // }
                    } else if("email".equals(fieldName)){
                        String newEmail = newValue.toString();
                        accountRepository.existsByEmail(newEmail).orElseThrow(() -> new EntityExistsException("Email already exist"));
                        field.set(account, newEmail);
                    }
                    else {
                        field.set(account, newValue);
                    }
                }
            } catch (NoSuchFieldException e) {
                throw new IllegalArgumentException("Field not found: " + fieldName);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Unable to update field: " + fieldName, e);
            }
        }

        Account updatedAccount = accountRepository.save(account);
        AccountResponse cachedAccount = AccountMapper.convertToResponse(updatedAccount);

        redisService.del("allAccount");
        redisService.del("account" + updatedAccount.getId());

        redisService.setObject("account" + updatedAccount.getId(), cachedAccount, 600);

        return cachedAccount;
    }

    // Xóa tài khoản
    @Transactional
    @Override
    public void deleteAccount(UUID id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found"));

        //kiem tra qua email
        String currentUserEmail = AuthUtil.AuthCheck();
        if (!currentUserEmail.equals(account.getEmail())) {
            throw new SecurityException("User is not authorized to delete this account");
        }

        redisService.del("allAccount");
        redisService.del("account:" + account.getId());

        accountRepository.deleteById(id);
    }
}
