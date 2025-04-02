package com.example.demo.controller;


import com.example.demo.common.DataResponse;
import com.example.demo.dto.AccountDTO;
import com.example.demo.dto.request.auth.ForgotPasswordRequest;
import com.example.demo.dto.request.auth.LoginRequest;
import com.example.demo.dto.request.auth.RegisterRequest;
import com.example.demo.dto.response.AccountResponse;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.auth.RegisterReponse;
import com.example.demo.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // Lấy tất cả tài khoản
    @GetMapping
    public ResponseEntity<DataResponse<List<AccountResponse>>> getAllAccounts() {
            return ResponseEntity.ok(DataResponse.<List<AccountResponse>>builder()
                    .success(true)
                    .message("Account retrieved successfully")
                    .data(accountService.getAllAccounts())
                    .build());
    }

    // Lấy tài khoản theo id
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<AccountResponse>> getAccountById(@PathVariable UUID id) {
        return ResponseEntity.ok(DataResponse.<AccountResponse>builder()
                .success(true)
                .message("Account retrieved successfully")
                .data(accountService.getAccount(id))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<DataResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<LoginResponse>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(accountService.login(loginRequest))
                        .build());
    }

    @PostMapping("/register")
    public ResponseEntity<DataResponse<RegisterReponse>> register(@RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<RegisterReponse>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(accountService.register(registerRequest))
                        .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<DataResponse<?>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        String email = request.getEmail();

        accountService.forgotPassword(email);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<String>builder()
                        .success(true)
                        .message("Send email successfully!")
                        .build());
    }



    @PostMapping("/logout")
    public ResponseEntity<DataResponse<AccountResponse>> logout(@RequestBody AccountDTO accountDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<AccountResponse>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(null)
                        .build());
    }

    // Tạo tài khoản mới
    @PostMapping
    public ResponseEntity<DataResponse<?>> createAccount(@RequestBody AccountDTO accountDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<AccountResponse>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(accountService.createAccount(accountDTO))
                        .build());
    }

    // Cập nhật tài khoản
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<?>> updateAccount(@PathVariable UUID id, @RequestBody AccountDTO updatedAccount) {
            return ResponseEntity.ok(DataResponse.<AccountResponse>builder()
                    .success(true)
                    .message("Account updated successfully!")
                    .data(accountService.updateAccount(id, updatedAccount))
                    .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<?>> partialUpdateAccount(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate ) {

        return ResponseEntity.ok(DataResponse.<AccountResponse>builder()
                .success(true)
                .message("Account updated successfully!")
                .data(accountService.partialUpdateAccount(id, fieldsToUpdate ))
                .build());
    }


    // Xóa tài khoản
    @DeleteMapping("/{id}")
    public ResponseEntity<DataResponse<?>> deleteAccount(@PathVariable UUID id) {
            accountService.deleteAccount(id);
            return ResponseEntity.ok(DataResponse.builder()
                    .success(true)
                    .message("Account deleted successfully!")
                    .build());

    }

}

