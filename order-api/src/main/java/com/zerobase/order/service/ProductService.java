package com.zerobase.order.service;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    /**
     * 상품 추가
     * @param sellerId
     * @param addProductForm
     * @return
     */
    @Transactional
    public Product addProduct(Long sellerId, AddProductForm addProductForm){
        return productRepository.save(Product.of(sellerId, addProductForm));
    }

}
