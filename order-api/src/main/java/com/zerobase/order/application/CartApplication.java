package com.zerobase.order.application;

import static com.zerobase.order.exception.ErrorCode.ITEM_COUNT_NOT_ENOUGH;
import static com.zerobase.order.exception.ErrorCode.NOT_FOUND_PRODUCT;

import com.zerobase.order.domain.model.Product;
import com.zerobase.order.domain.model.ProductItem;
import com.zerobase.order.domain.product.AddProductCartForm;
import com.zerobase.order.domain.redis.Cart;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.service.CartService;
import com.zerobase.order.service.ProductSearchService;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartApplication {

    private final CartService cartService;
    private final ProductSearchService productSearchService;

    /**
     * 장바구니 추가
     * @param customerId
     * @param addProductCartForm
     * @return
     */
    public Cart addCart(Long customerId, AddProductCartForm addProductCartForm){
        Product product = productSearchService.getByProductId(addProductCartForm.getId());

        if(product == null){
            throw new CustomException(NOT_FOUND_PRODUCT);
        }

        Cart cart = cartService.getCart(customerId);

        if(cart != null && !addAble(cart, product, addProductCartForm)){
            throw new CustomException(ITEM_COUNT_NOT_ENOUGH);
        }

        return cartService.addCart(customerId, addProductCartForm);
    }

    /**
     * 수량 비교
     * @param cart
     * @param product
     * @param addProductCartForm
     * @return
     */
    private boolean addAble(Cart cart, Product product, AddProductCartForm addProductCartForm){
        Cart.Product cartProduct = cart.getProducts().stream().filter(product1 ->
                product1.getId().equals(addProductCartForm.getId()))
            .findFirst()
            .orElseThrow(() -> new CustomException(NOT_FOUND_PRODUCT));

        Map<Long, Integer> cartItemCountMap = cartProduct.getProductItems().stream()
            .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCount = product.getProductItemList().stream()
            .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return addProductCartForm.getProductItems().stream()
            .noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    Integer currentCount = currentItemCount.get(formItem.getId());

                    return (formItem.getCount() + cartCount > currentCount);
                });
    }
}
