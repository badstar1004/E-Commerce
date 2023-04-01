package com.zerobase.user.application;

import static com.zerobase.domain.common.UserType.CUSTOMER;
import static com.zerobase.domain.common.UserType.SELLER;
import static com.zerobase.user.exception.ErrorCode.LOGIN_CHECK_FAIL;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.user.domain.SignInForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.service.customer.CustomerService;
import com.zerobase.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignInApplication {

    private final CustomerService customerService;
    private final JwtAuthenticationProvider jwtProvider;

    private final SellerService sellerService;

    /**
     * Customer 로그인
     *
     * @param signInForm
     * @return
     */
    public String customerLoginToken(SignInForm signInForm) {
        // 1. 로그인 가능 여부
        Customer customer =
            customerService.findValidCustomer(signInForm.getEmail(), signInForm.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        // 2. 토큰 발행 (별도 모듈)
        // 3. 토큰 response 함
        return jwtProvider.createToken(customer.getEmail(), customer.getId(), CUSTOMER);
    }

    /**
     * Seller 로그인
     *
     * @param signInForm
     * @return
     */
    public String sellerLoginToken(SignInForm signInForm) {
        // 1. 로그인 가능 여부
        Seller seller =
            sellerService.findValidSeller(signInForm.getEmail(), signInForm.getPassword())
                .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        // 2. 토큰 발행 (별도 모듈)
        // 3. 토큰 response 함
        return jwtProvider.createToken(seller.getEmail(), seller.getId(), SELLER);
    }
}
