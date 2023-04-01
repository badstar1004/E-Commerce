package com.zerobase.user.repository;

import com.zerobase.user.domain.model.CustomerBalanceHistory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface CustomerBalanceHistoryRepository extends JpaRepository<CustomerBalanceHistory, Long> {

    /**
     * 가장 최근 금액 조회
     * @param customerId
     * @return
     */
    Optional<CustomerBalanceHistory> findFirstByCustomer_IdOrderByIdDesc(
        @RequestParam("customer_id") Long customerId);
}
