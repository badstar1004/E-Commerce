package com.zerobase.order.service;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.repository.ProductRepository;
import com.zerobase.order.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private final ProductRepository productRepository;

    /**
     * 상품 상세페이지 구현
     * @param productId
     * @return
     */
    public Product getByProductId(Long productId){
        return productRepository.findWithProductItemsById(productId)
            .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));
    }

    /**
     * 상품 목록
     * @param productIds
     * @return
     */
    public List<Product> getListProductList(List<Long> productIds){
        return productRepository.findAllByIdIn(productIds);
    }

    /**
     * 검색어 기준 조회 (%name%)
     * @param name
     * @return
     */
    public List<Product> searchByName(String name){
        return productRepository.searchByName(name);
    }
}
