package com.zerobase.user.domain.customer;

import com.zerobase.user.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String email;
    private Integer balance;

    /**
     * Customer -> CustomerDto
     * @param customer
     * @return
     */
    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getEmail(), (customer.getBalance() == null ? 0 : customer.getBalance()));
    }

}
