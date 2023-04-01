package com.zerobase.user.service;

import static com.zerobase.user.exception.ErrorCode.NOT_ENOUGH_BALANCE;
import static com.zerobase.user.exception.ErrorCode.NOT_FOUND_USER;

import com.zerobase.user.domain.customer.ChangeBalanceForm;
import com.zerobase.user.domain.model.CustomerBalanceHistory;
import com.zerobase.user.exception.CustomException;
import com.zerobase.user.repository.CustomerBalanceHistoryRepository;
import com.zerobase.user.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerBalanceService {

    private final CustomerBalanceHistoryRepository customerBalanceHistoryRepository;
    private final CustomerRepository customerRepository;

    @Transactional(noRollbackFor = {CustomException.class})
    public CustomerBalanceHistory changeBalance(Long customerId,
        ChangeBalanceForm changeBalanceForm) throws CustomException {
        CustomerBalanceHistory customerBalanceHistory =
            customerBalanceHistoryRepository.findFirstByCustomer_IdOrderByIdDesc(customerId)
                .orElse(CustomerBalanceHistory.builder()
                    .currentMoney(0)
                    .changeMoney(0)
                    .customer(customerRepository.findById(customerId)
                        .orElseThrow(() -> new CustomException(NOT_FOUND_USER)))
                    .build());
        
        // 마이너스인 경우
        if(customerBalanceHistory.getCurrentMoney() + changeBalanceForm.getMoney() < 0){
            throw new CustomException(NOT_ENOUGH_BALANCE);
        }

        customerBalanceHistory = CustomerBalanceHistory.builder()
            .changeMoney(changeBalanceForm.getMoney())
            .currentMoney(customerBalanceHistory.getCurrentMoney() + changeBalanceForm.getMoney())
            .description(changeBalanceForm.getMessage())
            .fromMessage(changeBalanceForm.getFrom())
            .customer(customerBalanceHistory.getCustomer())
            .build();

        customerBalanceHistory.getCustomer().setBalance(customerBalanceHistory.getCurrentMoney());

        return customerBalanceHistoryRepository.save(customerBalanceHistory);
    }
}
