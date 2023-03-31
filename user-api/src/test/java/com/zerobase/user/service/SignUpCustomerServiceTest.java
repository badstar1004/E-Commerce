package com.zerobase.user.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Customer;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SignUpCustomerServiceTest {

    @Autowired
    private SignUpCustomerService signUpCustomerService;

    @Test
    @DisplayName("회원가입 테스트")
    void signUp() {
        SignUpForm signUpForm = SignUpForm.builder()
            .email("sss@naver.com")
            .name("nana")
            .password("1111")
            .birth(LocalDate.now())
            .phone("010-2222-4444")
            .build();

        // 항목별 확인이 좋음
        Customer customer = signUpCustomerService.signUp(signUpForm);

        assertNotNull(customer.getId());
        assertEquals("sss@naver.com", customer.getEmail());
        assertEquals("nana", customer.getName());
        assertEquals("1111", customer.getPassword());
        assertEquals("010-2222-4444", customer.getPhone());
    }
}