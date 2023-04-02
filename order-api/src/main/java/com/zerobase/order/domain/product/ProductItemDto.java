package com.zerobase.order.domain.product;

import com.zerobase.order.domain.model.ProductItem;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ProductItemDto {

    private Long id;
    private String name;
    private Integer price;
    private Integer count;

    public static ProductItemDto from(ProductItem productItem){
        return ProductItemDto.builder()
            .id(productItem.getId())
            .name(productItem.getName())
            .price(productItem.getPrice())
            .count(productItem.getCount())
            .build();
    }
}
