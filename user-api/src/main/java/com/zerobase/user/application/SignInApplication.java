package com.zerobase.user.application;

import static com.zerobase.domain.common.UserType.CUSTOMER;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.user.domain.SignInForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider jwtProvider;
    
    /**
     * Customer 로그인
     * @param signInForm
     * @return
     */
    public String customerLoginToken(SignInForm signInForm) {
        // 1. 로그인 가능 여부
        Customer customer =
            customerService.findValidCustomer(signInForm.getEmail(), signInForm.getPassword())
                .orElseThrow(() -> new CustomException(ErrorCode.LOGIN_CHECK_FAIL));

        // 2. 토큰 발행 (별도 모듈)
        // 3. 토큰 response 함
        return jwtProvider.createToken(customer.getEmail(), customer.getId(), CUSTOMER);
    }
}
