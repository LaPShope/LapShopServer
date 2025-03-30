package com.example.demo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountServiceTest {

//    @Mock
//    private AccountRepository accountRepository;
//
//    @InjectMocks
//    private AccountServiceImpl accountService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }

    class MyClass {
        public int add(int a, int b) {
            return a + b;
        }
    }

    @Test
    void testFunc() {
        MyClass myClass = new MyClass();

        assertEquals(5, myClass.add(2, 3), "2 + 3 should equal 5");

    }


}
