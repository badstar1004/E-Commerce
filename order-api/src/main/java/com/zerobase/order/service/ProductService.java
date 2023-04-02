package com.zerobase.order.service;

import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_ITEM;
import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.model.ProductItem;
import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.product.UpdateProductForm;
import com.zerobase.order.domain.product.UpdateProductItemForm;
import com.zerobase.order.domain.repository.ProductRepository;
import com.zerobase.order.exception.CustomException;
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

    /**
     * 상품 수정
     * @param sellerId
     * @param updateProductForm
     * @return
     */
    @Transactional
    public Product updateProduct(Long sellerId, UpdateProductForm updateProductForm){
        Product product = productRepository.findBySellerIdAndId(sellerId, updateProductForm.getId())
            .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        product.setName(updateProductForm.getName());
        product.setDescription(updateProductForm.getDescription());

        for(UpdateProductItemForm itemForm : updateProductForm.getItems()){
            ProductItem productItem = product.getProductItemList().stream()
                .filter(pi -> pi.getId().equals(itemForm.getId()))
                .findFirst().orElseThrow(() -> new CustomException(NOT_FOUND_ITEM));

            productItem.setName(itemForm.getName());
            productItem.setPrice(itemForm.getPrice());
            productItem.setCount(itemForm.getCount());
        }

        return product;
    }
}
