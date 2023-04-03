package com.zerobase.order.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.product.AddProductCartForm;
import com.zerobase.order.domain.product.AddProductForm;
import com.zerobase.order.domain.product.AddProductItemForm;
import com.zerobase.order.domain.redis.Cart;
import com.zerobase.order.domain.repository.ProductRepository;
import com.zerobase.order.service.ProductService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CartApplicationTest {

    @Autowired
    private CartApplication cartApplication;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void addTestModify() {

        // 카트 추가
        Long customerId = 100L;

        cartApplication.clearCart(customerId);

        // 상품 추가
        Product p = addProduct();
        Product result = productRepository.findWithProductItemsById(p.getId()).get();

        assertNotNull(result);

        assertEquals("나이키 에어포스", result.getName());
        assertEquals("나이키 신발입니다.", result.getDescription());

        assertEquals(3, result.getProductItemList().size());
        assertEquals("나이키 에어포스0", result.getProductItemList().get(0).getName());
        assertEquals(10000, result.getProductItemList().get(0).getPrice());
        assertEquals(10, result.getProductItemList().get(0).getCount());


        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));
        
        // 데이터가 잘 들어 갔는지 확인이 필요함 (개별로 해야함)
        assertEquals(0, cart.getMessages().size());

        cart = cartApplication.getCart(customerId);
        assertEquals(1, cart.getMessages().size());
    }

    AddProductCartForm makeAddForm(Product p){
        AddProductCartForm.ProductItem productItem =
            AddProductCartForm.ProductItem.builder()
                .id(p.getProductItemList().get(0).getId())
                .name(p.getProductItemList().get(0).getName())
                .price(p.getProductItemList().get(0).getPrice())
                .count(5)
                .price(20000)
                .build();

        AddProductCartForm addProductCartForm =
            AddProductCartForm.builder()
                .id(p.getId())
                .sellerId(p.getSellerId())
                .name(p.getName())
                .description(p.getDescription())
                .productItems(List.of(productItem))
                .build();

        return addProductCartForm;
    }

    Product addProduct() {
        Long sellerId = 1L;

        AddProductForm addProductForm = makeProductForm("나이키 에어포스", "나이키 신발입니다.", 3);

        return productService.addProduct(sellerId, addProductForm);
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
            .count(10)
            .build();
    }
}