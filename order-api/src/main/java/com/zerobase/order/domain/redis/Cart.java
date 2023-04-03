package com.zerobase.order.domain.redis;

import com.zerobase.order.domain.product.AddProductCartForm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

@Data
@NoArgsConstructor
@RedisHash("Cart")
public class Cart {

    @Id
    private Long customerId;

    private List<Product> products = new ArrayList<>();

    private List<String> messages = new ArrayList<>();

    /**
     * 생성자
     * @param customerId
     */
    public Cart(Long customerId){
        this.customerId = customerId;
    }

    /**
     * @param message
     */
    public void addMessage(String message) {
        messages.add(message);
    }

    /**
     * 정보가 수정되었을 때 알려주려는 의도의 클래스
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Product {

        private Long id;
        private Long sellerId;
        private String name;
        private String description;
        private List<ProductItem> productItems = new ArrayList<>();

        /**
         * Product from
         *
         * @param addProductCartForm
         * @return
         */
        public static Product from(AddProductCartForm addProductCartForm) {
            return Product.builder()
                .id(addProductCartForm.getId())
                .sellerId(addProductCartForm.getSellerId())
                .name(addProductCartForm.getName())
                .description(addProductCartForm.getDescription())
                .productItems(addProductCartForm.getProductItems()
                    .stream().map(ProductItem::from).collect(Collectors.toList()))
                .build();
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductItem {

        private Long id;
        private String name;
        private Integer price;
        private Integer count;

        /**
         * ProductItem from
         *
         * @param productItemForm
         * @return
         */
        public static ProductItem from(AddProductCartForm.ProductItem productItemForm) {
            return ProductItem.builder()
                .id(productItemForm.getId())
                .name(productItemForm.getName())
                .price(productItemForm.getPrice())
                .count(productItemForm.getCount())
                .build();
        }
    }
}
