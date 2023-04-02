package com.zerobase.order.controller;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.product.ProductDto;
import com.zerobase.order.domain.product.ProductItemDto;
import com.zerobase.order.domain.product.UpdateProductForm;
import com.zerobase.order.domain.product.UpdateProductItemForm;
import com.zerobase.order.service.ProductItemService;
import com.zerobase.order.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/seller/product")
@RequiredArgsConstructor
public class SellerProductController {

    private final ProductService productService;
    private final ProductItemService productItemService;
    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    /**
     * 상품 추가
     * @param token
     * @param addProductForm
     * @return
     */
    @PostMapping
    public ResponseEntity<ProductDto> addProduct(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody AddProductForm addProductForm) {

        return ResponseEntity.ok(ProductDto.from(
            productService.addProduct(jwtAuthenticationProvider.getUserVo(token).getId(),
                addProductForm)));
    }

    /**
     * 상품 아이템 추가
     * @param token
     * @param addProductItemForm
     * @return
     */
    @PostMapping("/item")
    public ResponseEntity<ProductDto> addProductItem(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody AddProductItemForm addProductItemForm) {

        return ResponseEntity.ok(ProductDto.from(
            productItemService.addProductItem(jwtAuthenticationProvider.getUserVo(token).getId(),
                addProductItemForm)));
    }

    /**
     * 상품 수정
     * @param token
     * @param updateProductForm
     * @return
     */
    @PutMapping
    public ResponseEntity<ProductDto> updateProduct(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody UpdateProductForm updateProductForm) {

        return ResponseEntity.ok(ProductDto.from(
            productService.updateProduct(jwtAuthenticationProvider.getUserVo(token).getId(),
                updateProductForm)));
    }

    /**
     * 상품 아이템 수정
     * @param token
     * @param updateProductItemForm
     * @return
     */
    @PutMapping("/item")
    public ResponseEntity<ProductItemDto> addProductItem(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody UpdateProductItemForm updateProductItemForm) {

        return ResponseEntity.ok(ProductItemDto.from(
            productItemService.updateProductItem(jwtAuthenticationProvider.getUserVo(token).getId(),
                updateProductItemForm)));
    }

    /**
     * 상품 삭제
     * @param token
     * @param id
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteProduct(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestParam Long id) {

        productService.deleteProduct(jwtAuthenticationProvider.getUserVo(token).getId(), id);
        return ResponseEntity.ok().build();
    }

    /**
     * 상품 아이템 삭제
     * @param token
     * @param id
     * @return
     */
    @DeleteMapping("/item")
    public ResponseEntity<Void> deleteProductItem(@RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestParam Long id) {

        productItemService.deleteProductItem(jwtAuthenticationProvider.getUserVo(token).getId(), id);
        return ResponseEntity.ok().build();
    }
}
