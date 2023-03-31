package com.zerobase.user.service;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.exception.ErrorCode;
import com.zerobase.user.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SignUpCustomerService {

    private final CustomerRepository customerRepository;

    /**
     * 회원 가입
     *
     * @param signUpForm
     * @return
     */
    public Customer signUp(SignUpForm signUpForm) {
        return customerRepository.save(Customer.from(signUpForm));
    }

    /**
     * 이메일 존재 여부
     *
     * @param email
     * @return
     */
    public boolean isEmailExists(String email) {
        return customerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .isPresent();
    }

    /**
     * 이메일 인증 폼
     *
     * @param customerId
     * @param verificationCode
     * @return
     */
    @Transactional
    public LocalDateTime changeCustomerValidateEmail(Long customerId, String verificationCode) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            customer.setVerificationCode(verificationCode);
            customer.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

            return customer.getVerifyExpiredAt();
        }

        throw new CustomException(ErrorCode.NOT_FOUND_USER);
    }


    /**
     * 이메일 인증
     * @param email
     * @param code
     * @throws CustomException
     */
    @Transactional
    public void verifyEmail(String email, String code) throws CustomException {
        Customer customer = customerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        if (customer.isVerify()) {
            // 인증 여부
            throw new CustomException(ErrorCode.ALREADY_VERIFY);
        } else if (!customer.getVerificationCode().equals(code)) {
            // 잘못된 인증
            throw new CustomException(ErrorCode.WRONG_VERIFICATION);
        }else if(customer.getVerifyExpiredAt().isBefore(LocalDateTime.now())){
            // 인증 시간 (2분)
            throw new CustomException(ErrorCode.EXPIRE_CODE);
        }

        customer.setVerify(true);
    }
}
