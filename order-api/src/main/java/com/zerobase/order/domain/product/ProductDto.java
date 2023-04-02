package com.zerobase.order.domain.product;

import com.zerobase.order.domain.model.Product;
import java.util.List;
import java.util.stream.Collectors;
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
public class ProductDto {

    private Long id;
    private String name;
    private String description;
    private List<ProductItemDto> productItemDtoList;

    public static ProductDto from(Product product){
        List<ProductItemDto> productItemDtos = product.getProductItemList()
            .stream().map(ProductItemDto::from).collect(Collectors.toList());

        return ProductDto.builder()
            .id(product.getId())
            .name(product.getName())
            .description(product.getDescription())
            .productItemDtoList(productItemDtos)
            .build();
    }
}
