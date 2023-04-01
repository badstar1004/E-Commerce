package com.zerobase.user.service.seller;

import static com.zerobase.user.exception.ErrorCode.ALREADY_VERIFY;
import static com.zerobase.user.exception.ErrorCode.EXPIRE_CODE;
import static com.zerobase.user.exception.ErrorCode.NOT_FOUND_USER;
import static com.zerobase.user.exception.ErrorCode.WRONG_VERIFICATION;

import com.zerobase.user.domain.SignUpForm;
import com.zerobase.user.domain.model.Seller;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.repository.SellerRepository;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SellerService {

    private final SellerRepository sellerRepository;

    /**
     * 조회
     *
     * @param id
     * @param email
     * @return
     */
    public Optional<Seller> findByIdAndEmail(Long id, String email) {
        return sellerRepository.findByIdAndEmail(id, email);
    }

    /**
     * 조회 (이메일, 패스워드 유효성 검사)
     *
     * @param email
     * @param password
     * @return
     */
    public Optional<Seller> findValidSeller(String email, String password) {
        return sellerRepository.findByEmailAndPasswordAndVerifyIsTrue(email, password);
    }

    /**
     * 회원 가입
     *
     * @param signUpForm
     * @return
     */
    @Transactional
    public Seller signUp(SignUpForm signUpForm) {
        return sellerRepository.save(Seller.from(signUpForm));
    }

    /**
     * 이메일 존재 여부
     *
     * @param email
     * @return
     */
    public boolean isEmailExists(String email) {
        return sellerRepository.findByEmail(email.toLowerCase(Locale.ROOT))
            .isPresent();
    }

    /**
     * 이메일 인증 폼
     *
     * @param sellerId
     * @param verificationCode
     * @return
     */
    @Transactional
    public LocalDateTime changeSellerValidateEmail(Long sellerId, String verificationCode) {
        Optional<Seller> sellerOptional = sellerRepository.findById(sellerId);

        if (sellerOptional.isPresent()) {
            Seller seller = sellerOptional.get();
            seller.setVerificationCode(verificationCode);
            seller.setVerifyExpiredAt(LocalDateTime.now().plusDays(1));

            return seller.getVerifyExpiredAt();
        }

        throw new CustomException(NOT_FOUND_USER);
    }

    /**
     * 이메일 인증 예외
     *
     * @param email
     * @param code
     */
    @Transactional
    public void verifyEmail(String email, String code) {
        Seller seller = sellerRepository.findByEmail(email)
            .orElseThrow(() -> new CustomException(NOT_FOUND_USER));

        if (seller.isVerify()) {
            // 인증 여부
            throw new CustomException(ALREADY_VERIFY);
        } else if (!seller.getVerificationCode().equals(code)) {
            // 잘못된 인증
            throw new CustomException(WRONG_VERIFICATION);
        } else if (seller.getVerifyExpiredAt().isBefore(LocalDateTime.now())) {
            // 인증 시간
            throw new CustomException(EXPIRE_CODE);
        }

        seller.setVerify(true);
    }
}
