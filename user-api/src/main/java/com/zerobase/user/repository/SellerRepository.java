package com.zerobase.user.repository;

import com.zerobase.user.domain.model.Seller;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerRepository extends JpaRepository<Seller, Long> {

    /**
     * 이메일 조회
     * @param email
     * @return
     */
    Optional<Seller> findByEmail(String email);

    /**
     * 조회
     * @param id
     * @param email
     * @return
     */
    Optional<Seller> findByIdAndEmail(Long id, String email);

    /**
     * 조회 (이메일, 패스워드 유효성 검사)
     * @param email
     * @param password
     * @return
     */
    Optional<Seller> findByEmailAndPasswordAndVerifyIsTrue(String email, String password);
}
