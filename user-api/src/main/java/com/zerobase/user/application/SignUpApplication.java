package com.zerobase.user.application;

import static com.zerobase.user.exception.ErrorCode.ALREADY_REGISTER_USER;

import com.zerobase.user.client.MailgunClient;
import com.zerobase.user.client.mailgun.SendMailForm;
import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Customer;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.service.customer.SignUpCustomerService;
import com.zerobase.user.service.seller.SellerService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpApplication {

    private final MailgunClient mailgunClient;
    private final SignUpCustomerService signUpCustomerService;
    private final SellerService sellerService;

    /**
     * 회원 가입 (고객)
     *
     * @param signUpForm
     * @return
     */
    public String customerSignUp(SignUpForm signUpForm) {
        if (signUpCustomerService.isEmailExists(signUpForm.getEmail())) {
            // 이메일 예외 발생
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {        // 계정이 없다면 계정 생성
            // 계정 생성
            Customer customer = signUpCustomerService.signUp(signUpForm);

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                .from("badstar1004@gmail.com")
                .to(customer.getEmail())
                .subject("Verification Email!")
                .text(getVerificationEmailBody(
                    customer.getEmail(), customer.getName(), "customer", code))
                .build();

            // 이메일
            mailgunClient.sendEmail(sendMailForm);
            // 이메일 인증폼
            signUpCustomerService.changeCustomerValidateEmail(customer.getId(), code);

            return "회원가입에 성공하였습니다.";
        }
    }

    /**
     * 회원 가입 (셀러)
     *
     * @param signUpForm
     * @return
     */
    public String sellerSignUp(SignUpForm signUpForm) {
        if (sellerService.isEmailExists(signUpForm.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            Seller seller = sellerService.signUp(signUpForm);

            String code = getRandomCode();

            SendMailForm sendMailForm = SendMailForm.builder()
                .from("badstar1004@gmail.net")
                .to(seller.getEmail())
                .subject("Verification Email!")
                .text(getVerificationEmailBody(
                    seller.getEmail(), seller.getName(), "seller", code
                ))
                .build();

            mailgunClient.sendEmail(sendMailForm);
            sellerService.changeSellerValidateEmail(seller.getId(), code);

            return "회원가입에 성공하였습니다.";
        }
    }

    /**
     * @return
     */
    private String getRandomCode() {
        return RandomStringUtils.random(10, true, true);
    }

    /**
     * 인증 url
     *
     * @return
     */
    private String getVerificationEmailBody(String email, String name, String type, String code) {
        StringBuilder sb = new StringBuilder();
        return sb.append("Hello ").append(name).append("! Please Click Link for verification.\n\n")
            .append("http://localhost:8081/signup/" + type + "/verify/?email=")
            .append(email)
            .append("&code=")
            .append(code).toString();
    }

    /**
     * 이메일 인증 (고객)
     *
     * @param email
     * @param code
     */
    public void customerVerify(String email, String code) {
        signUpCustomerService.verifyEmail(email, code);
    }

    /**
     * 이메일 인증 (셀러)
     *
     * @param email
     * @param code
     */
    public void sellerVerify(String email, String code) {
        sellerService.verifyEmail(email, code);
    }
}
