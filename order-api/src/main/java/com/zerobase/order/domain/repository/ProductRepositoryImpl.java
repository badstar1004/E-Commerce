package com.zerobase.order.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.model.QProduct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 조회 쿼리
     * @param name
     * @return
     */
    @Override
    public List<Product> searchByName(String name) {
        // 명시적 "%"
        String searchName = "%" + name + "%";
        QProduct qProduct = QProduct.product;

        return jpaQueryFactory.selectFrom(qProduct)
            .where(qProduct.name.like(searchName))
            .fetch();
    }
}
