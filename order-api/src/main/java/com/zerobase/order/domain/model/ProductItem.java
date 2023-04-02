package com.zerobase.order.domain.model;

import com.zerobase.order.domain.product.AddProductItemForm;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)
public class ProductItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    @Audited
    private String name;

    @Audited
    private Integer price;

    private Integer count;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    /**
     * 매개변수 1개 : from, 2개 이상: of
     * ProductItem builder()
     * @param sellerId
     * @param addProductItemForm
     * @return
     */
    public static ProductItem of(Long sellerId, AddProductItemForm addProductItemForm){
        return ProductItem.builder()
            .sellerId(sellerId)
            .name(addProductItemForm.getName())
            .price(addProductItemForm.getPrice())
            .count(addProductItemForm.getCount())
            .build();
    }
}
