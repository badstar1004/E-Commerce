package com.zerobase.order.domain.repository;

import com.zerobase.order.domain.model.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @EntityGraph(attributePaths = {"productItemList"}, type = EntityGraphType.LOAD)
    Optional<Product> findWithProductItemsById(Long id);

    @EntityGraph(attributePaths = {"productItemList"}, type = EntityGraphType.LOAD)
    Optional<Product> findBySellerIdAndId(Long sellerId, Long id);

    @EntityGraph(attributePaths = {"productItemList"}, type = EntityGraphType.LOAD)
    List<Product> findAllByIdIn(List<Long> ids);
}
