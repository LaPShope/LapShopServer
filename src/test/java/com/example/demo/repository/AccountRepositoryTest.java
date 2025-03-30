package com.example.demo.repository;

import com.example.demo.common.Enums;
import com.example.demo.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class AccountRepositoryTest {
    @Autowired
    private AccountRepository accountRepository;


    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
    }

    @Test
    void givenAccount_whenSave_thenSuccess() {
        // Arrange
        Account account = Account.builder()
                .email("Triet")
                .password("123456")
                .name("Triet")
                .role(Enums.Role.Customer)
                .build();

        // Act
        Account accountSaved = accountRepository.save(account);

        assertNotNull(accountSaved);
        assertNotNull(accountSaved.getId());
        assertEquals("Triet", accountSaved.getEmail());
        assertEquals("123456", accountSaved.getPassword());
        assertEquals("Triet", accountSaved.getName());
        assertEquals(Enums.Role.Customer, accountSaved.getRole());
    }

    @Test
    void getAccount_compareRole_thenSuccess() {
        // Arrange
        Account baseAccount = Account.builder()
                .email("Triet")
                .password("123456")
                .name("Triet")
                .role(Enums.Role.Customer)
                .build();

        accountRepository.save(baseAccount);
        // Act
        Account acc = accountRepository.findByEmail("Triet")
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Assert
        assertEquals("Customer", acc.getRole().value(), "Role should be Customer");
    }

}
