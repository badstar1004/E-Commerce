package com.zerobase.user.controller;

import com.zerobase.user.application.SignUpApplication;
import com.zerobase.user.domain.SignUpForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signup")
@RequiredArgsConstructor
public class SignUpController {

    private final SignUpApplication signUpApplication;

    /**
     * 회원가입 후 메일건 발송 (고객)
     *
     * @param signUpForm
     * @return
     */
    @PostMapping("/customer")
    public ResponseEntity<String> customerSignUp(@RequestBody SignUpForm signUpForm) {
        return ResponseEntity.ok(signUpApplication.customerSignUp(signUpForm));
    }

    @GetMapping("/customer/verify")
    public ResponseEntity<String> verifyCustomer(String email, String code) {
        signUpApplication.customerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }

    /**
     * 회원가입 후 메일건 발송 (셀러)
     *
     * @param signUpForm
     * @return
     */
    @PostMapping("/seller")
    public ResponseEntity<String> sellerSignUp(@RequestBody SignUpForm signUpForm) {
        return ResponseEntity.ok(signUpApplication.sellerSignUp(signUpForm));
    }

    @GetMapping("/seller/verify")
    public ResponseEntity<String> verifySeller(String email, String code) {
        signUpApplication.sellerVerify(email, code);
        return ResponseEntity.ok("인증이 완료되었습니다.");
    }
}
