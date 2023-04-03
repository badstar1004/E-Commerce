package com.zerobase.order.domain.model;

import com.zerobase.order.domain.product.AddProductForm;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
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
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long sellerId;

    private String name;

    private String description;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "product_id")
    private List<ProductItem> productItemList = new ArrayList<>();

    /**
     * Product of
     *
     * @param addProductForm
     * @return
     */
    public static Product of(Long sellerId, AddProductForm addProductForm) {
        return Product.builder()
            .id(addProductForm.getId())
            .sellerId(sellerId)
            .name(addProductForm.getName())
            .description(addProductForm.getDescription())
            .productItemList(addProductForm.getItems().stream()
                .map(piForm -> ProductItem.of(sellerId, piForm)).collect(Collectors.toList()))
            .build();
    }

}
