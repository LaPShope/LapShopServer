package com.example.demo.Controller;


import com.example.demo.Common.DataResponse;
import com.example.demo.DTO.AccountDTO;
import com.example.demo.Service.AccountService;
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
    public ResponseEntity<DataResponse<List<AccountDTO>>> getAllAccounts() {

            return ResponseEntity.ok(DataResponse.<List<AccountDTO>>builder()
                    .success(true)
                    .message("Account retrieved successfully")
                    .data(accountService.getAllAccounts())
                    .build());

    }

    // Lấy tài khoản theo id
    @GetMapping("/{id}")
    public ResponseEntity<DataResponse<AccountDTO>> getAccountById(@PathVariable UUID id) {
        AccountDTO accountDTO = accountService.getAccountById(id);
        return ResponseEntity.ok(DataResponse.<AccountDTO>builder()
                .success(true)
                .message("Account retrieved successfully")
                .data(accountDTO)
                .build());
    }

    // Tạo tài khoản mới
    @PostMapping
    public ResponseEntity<DataResponse<?>> createAccount(@RequestBody AccountDTO accountDTO) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DataResponse.<AccountDTO>builder()
                        .success(true)
                        .message("Account created successfully!")
                        .data(accountService.createAccount(accountDTO))
                        .build());
    }

    // Cập nhật tài khoản
    @PutMapping("/{id}")
    public ResponseEntity<DataResponse<?>> updateAccount(@PathVariable UUID id, @RequestBody AccountDTO updatedAccount) {
            return ResponseEntity.ok(DataResponse.<AccountDTO>builder()
                    .success(true)
                    .message("Account updated successfully!")
                    .data(accountService.updateAccount(id, updatedAccount))
                    .build());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResponse<?>> partialUpdateAccount(
            @PathVariable UUID id,
            @RequestBody Map<String, Object> fieldsToUpdate ) {

        return ResponseEntity.ok(DataResponse.<AccountDTO>builder()
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

