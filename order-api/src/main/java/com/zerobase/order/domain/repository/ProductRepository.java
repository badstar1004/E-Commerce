package com.zerobase.order.domain.repository;

import com.zerobase.order.domain.model.Product;
import java.util.Optional;
import javax.persistence.Entity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @EntityGraph(attributePaths = {"productItemList"}, type = EntityGraphType.LOAD)
    Optional<Product> findWithProductItemsById(Long id);

    @EntityGraph(attributePaths = {"productItemList"}, type = EntityGraphType.LOAD)
    Optional<Product> findBySellerIdAndId(Long sellerId, Long id);
}
