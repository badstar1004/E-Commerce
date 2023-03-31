package com.zerobase.user.controller;

import com.zerobase.user.application.SignInApplication;
import com.zerobase.user.domain.SignInForm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/signin")
public class SignInController {

    private final SignInApplication signInApplication;

    /**
     * Customer 로그인
     * @param signInForm
     * @return
     */
    @PostMapping("/customer")
    public ResponseEntity<String> signInCustomer(@RequestBody SignInForm signInForm) {
        return ResponseEntity.ok(signInApplication.customerLoginToken(signInForm));
    }
}
