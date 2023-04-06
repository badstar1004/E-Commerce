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
        assertEquals(5, result.getProductItemList().get(0).getCount());

        // 장바구니에 추가
        Cart cart = cartApplication.addCart(customerId, makeAddForm(result));

        // 데이터가 잘 들어 갔는지 확인이 필요함 (개별로 해야함)
        assertEquals(0, cart.getMessages().size());
        assertEquals(100, cart.getCustomerId());
        assertEquals(1, cart.getProducts().get(0).getId());
        assertEquals(1, cart.getProducts().get(0).getSellerId());
        assertEquals("나이키 에어포스", cart.getProducts().get(0).getName());
        assertEquals("나이키 신발입니다.", cart.getProducts().get(0).getDescription());
        assertEquals(1, cart.getProducts().get(0).getProductItems().get(0).getId());
        assertEquals("나이키 에어포스0", cart.getProducts().get(0).getProductItems().get(0).getName());
        assertEquals(10000, cart.getProducts().get(0).getProductItems().get(0).getPrice());
        assertEquals(5, cart.getProducts().get(0).getProductItems().get(0).getCount());

        cart = cartApplication.getCart(customerId);
        assertEquals(1, cart.getMessages().size());
    }

    /**
     * 장바구니 추가
     * @param p 상품
     * @return AddProductCartForm
     */
    AddProductCartForm makeAddForm(Product p){
        AddProductCartForm.ProductItem productItem =
            AddProductCartForm.ProductItem.builder()
                .id(p.getProductItemList().get(0).getId())
                .name(p.getProductItemList().get(0).getName())
                .price(p.getProductItemList().get(0).getPrice())
                .count(5)
                .price(10000)
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

    /**
     * 상품 추가 초기값
     * @return Product
     */
    Product addProduct() {
        Long sellerId = 1L;

        AddProductForm addProductForm = makeProductForm("나이키 에어포스", "나이키 신발입니다.", 3);

        return productService.addProduct(sellerId, addProductForm);
    }

    /**
     * 상품 추가 기능
     * @param name  상품명
     * @param description   상품 설명
     * @param itemCount 몇개의 상품
     * @return  AddProductForm
     */
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

    /**
     * 상품 아이템 추가
     * @param productId 상품 id
     * @param name  상품 아이템 명
     * @return  AddProductItemForm
     */
    private static AddProductItemForm makeProductItemForm(Long productId, String name){
        return AddProductItemForm.builder()
            .productId(productId)
            .name(name)
            .price(10000)
            .count(5)
            .build();
    }
}