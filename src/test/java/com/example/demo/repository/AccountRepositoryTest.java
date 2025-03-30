package com.example.demo.repository;

import com.example.demo.common.Enums;
import com.example.demo.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
public class AccountRepositoryTest {
    @Autowired
    AccountRepository accountRepository;

    @Test
    void givenAccount_whenSave_thenSuccess() {

        Account account = Account.builder()
                .email("Triet")
                .password("123456")
                .name("Triet")
                .role(Enums.role.CUSTOMER)
                .build();

        Account accountSaved = accountRepository.save(account);
        System.out.println(accountSaved.toString());


        System.out.println("accountSaved = " + accountSaved);

        assertNotNull(accountSaved);
        assertNotNull(accountSaved.getId());
        assertEquals(account.getEmail(), accountSaved.getEmail());
        assertEquals(account.getPassword(), accountSaved.getPassword());
        assertEquals(account.getName(), accountSaved.getName());
        assertEquals(account.getRole(), accountSaved.getRole());
    }
}
