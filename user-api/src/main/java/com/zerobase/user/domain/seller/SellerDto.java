package com.zerobase.user.domain.seller;

import com.zerobase.user.domain.model.Seller;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SellerDto {

    private Long id;
    private String email;

    /**
     * Seller -> SellerDto
     * @param seller
     * @return
     */
    public static SellerDto from(Seller seller){
        return new SellerDto(seller.getId(), seller.getEmail());
    }
}
