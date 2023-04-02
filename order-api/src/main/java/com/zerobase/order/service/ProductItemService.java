package com.zerobase.order.service;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;
import static com.zerobase.order.exception.ErrorCode.SAME_ITEM_NAME;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.model.ProductItem;
import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.repository.ProductItemRepository;
import com.zerobase.order.domain.repository.ProductRepository;
import com.zerobase.order.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductRepository productRepository;
    private final ProductItemRepository productItemRepository;

    @Transactional
    public Product addProductItem(Long sellerId, AddProductItemForm addProductItemForm){
        // Product 가 있는지 확인
        Product product = productRepository.findBySellerIdAndId(sellerId,
            addProductItemForm.getProductId())
            .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        if(product.getProductItemList().stream()
            .anyMatch(productItem -> productItem.getName().equals(addProductItemForm.getName()))){
            throw new CustomException(SAME_ITEM_NAME);
        }

        ProductItem productItem = ProductItem.of(sellerId, addProductItemForm);
        product.getProductItemList().add(productItem);

        return product;
    }
}
