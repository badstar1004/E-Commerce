package com.zerobase.user.domain.customer;

import com.zerobase.user.domain.model.Customer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerDto {

    private Long id;
    private String email;

    /**
     * Customer -> CustomerDto
     * @param customer
     * @return
     */
    public static CustomerDto from(Customer customer) {
        return new CustomerDto(customer.getId(), customer.getEmail());
    }

}
