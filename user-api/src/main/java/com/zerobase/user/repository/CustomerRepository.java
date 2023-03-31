package com.zerobase.user.repository;

import com.zerobase.user.domain.model.Customer;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {

    /**
     * 이메일 조회
     * @param email
     * @return
     */
    Optional<Customer> findByEmail(String email);

}
