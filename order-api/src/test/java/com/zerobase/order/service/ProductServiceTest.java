package com.zerobase.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.repository.ProductRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("상품 추가 테스트")
    void addProductTest() {
        Long sellerId = 1L;

        AddProductForm addProductForm = makeProductForm("나이키 에어포스", "나이키 신발입니다.", 3);

        Product product = productService.addProduct(sellerId, addProductForm);
        Product result = productRepository.findWithProductItemsById(product.getId()).get();

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("나이키 에어포스", result.getName());
        assertEquals("나이키 신발입니다.", result.getDescription());
        assertEquals(1L, result.getSellerId());
        assertEquals(3, result.getProductItemList().size());
        assertEquals("나이키 에어포스0", result.getProductItemList().get(0).getName());
        assertEquals(10000, result.getProductItemList().get(0).getPrice());
        assertEquals(1, result.getProductItemList().get(0).getCount());

//        for (int i = 0; i < product.getProductItemList().size(); i++) {
//            assertEquals(1L, product.getProductItemList().get(i).getSellerId());
//            assertEquals("나이키 에어포스" + i, product.getProductItemList().get(i).getName());
//            assertEquals(10000, product.getProductItemList().get(i).getPrice());
//            assertEquals(1, product.getProductItemList().get(i).getCount());
//        }
    }

    private static AddProductForm makeProductForm(String name, String description, int itemCount){
        List<AddProductItemForm> addProductItemFormList = new ArrayList<>();

        for (int i = 0; i < itemCount; i++) {
            addProductItemFormList.add(makeProductItemForm(null, name + i));
        }

        return AddProductForm.builder()
            .name(name)
            .description(description)
            .items(addProductItemFormList)
            .build();
    }

    private static AddProductItemForm makeProductItemForm(Long productId, String name){
        return AddProductItemForm.builder()
            .productId(productId)
            .name(name)
            .price(10000)
            .count(1)
            .build();
    }
}