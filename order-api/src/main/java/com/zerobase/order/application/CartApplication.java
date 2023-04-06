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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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

        if(!addAble(cart, product, addProductCartForm)){
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
            .orElse(Cart.Product.builder().id(product.getId()).productItems(Collections.emptyList())
                .build());


        Map<Long, Integer> cartItemCountMap = cartProduct.getProductItems().stream()
            .collect(Collectors.toMap(Cart.ProductItem::getId, Cart.ProductItem::getCount));

        Map<Long, Integer> currentItemCount = product.getProductItemList().stream()
            .collect(Collectors.toMap(ProductItem::getId, ProductItem::getCount));

        return addProductCartForm.getProductItems().stream()
            .noneMatch(
                formItem -> {
                    Integer cartCount = cartItemCountMap.get(formItem.getId());
                    if(cartCount == null){
                        cartCount = 0;
                    }

                    Integer currentCount = currentItemCount.get(formItem.getId());

                    return (formItem.getCount() + cartCount > currentCount);
                });
    }

    /**
     * 장바구니 조회
     * @param customerId
     * @return
     */
    // 1. 장바구니에 상품을 추가
    // 2. 상품의 가격이나 수량이 변동
    public Cart getCart(Long customerId){

        Cart cart = refreshCart(cartService.getCart(customerId));
        cartService.putCart(cart.getCustomerId(), cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setProducts(cart.getProducts());
        returnCart.setMessages(cart.getMessages());

        // 2. 메시지를 보고 난 다음, 이미 본 메시지는 제거
        // 메시지 없는것
        cart.setMessages(new ArrayList<>());
        cartService.putCart(customerId, cart);
        
        return returnCart;
    }

    /**
     * 장바구니 수정
     * @param customerId
     * @param cart
     * @return
     */
    public Cart updateCart(Long customerId, Cart cart){
        // 실질적으로 변하는 데이터
        // 상품의 삭제, 수량 변경

        cartService.putCart(customerId, cart);
        return getCart(customerId);
    }

    /**
     * 테스트 시
     * @param customerId
     */
    public void clearCart(Long customerId){
        cartService.putCart(customerId, null);
    }

    /**
     * 장바구니 수정사항 예외처리
     * @param cart
     * @return
     */
    protected Cart refreshCart(Cart cart){
        // 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크
        // 그에 맞는 알람을 제공
        // 2. 상품의 수량, 가격을 우리가 임의로 변경
        Map<Long, Product> productMap =
            productSearchService.getListProductList(cart.getProducts().stream().map(Cart.Product::getId).collect(
                Collectors.toList())).stream().collect(Collectors.toMap(Product::getId, product -> product));

        List<Product> products =
            productSearchService.getListProductList(new ArrayList<>(productMap.keySet()));

        for(int i = 0; i < cart.getProducts().size(); i++){
            Cart.Product cartProduct = cart.getProducts().get(i);

            Product p = productMap.get(cartProduct.getId());

            if(p == null){
                cart.getProducts().remove(cartProduct);
                i--;

                cart.addMessage(cartProduct.getName() + " 상품이 삭제되었습니다.");
                continue;
            }

            Map<Long, ProductItem> productItemMap = p.getProductItemList().stream()
                .collect(Collectors.toMap(ProductItem::getId, productItem -> productItem));

            // 해야할 것
            // 각 케이스 별로 예외처리, 예외가 정상 출력 되는지 확인해야함
            List<String> tempMessage = new ArrayList<>();
            for(int j = 0; j < cartProduct.getProductItems().size(); j++){
                Cart.ProductItem cartProductItem = cartProduct.getProductItems().get(j);
                ProductItem pi = productItemMap.get(cartProductItem.getId());

                if(pi == null){
                    cartProduct.getProductItems().remove(cartProductItem);
                    j--;

                    tempMessage.add(cartProductItem.getName() + " 옵션이 삭제되었습니다.");
                    continue;
                }

                boolean isPriceChange = false;
                boolean isCountNotEnough = false;
                // 가격
                if(!cartProductItem.getPrice().equals(pi.getPrice())){
                    isPriceChange = true;
                    cartProductItem.setPrice(pi.getPrice());
                }

                // 수량
                if(cartProductItem.getCount() < pi.getCount()){
                    isCountNotEnough = true;
                    cartProductItem.setCount(pi.getCount());
                }

                if(isPriceChange && isCountNotEnough){
                    tempMessage.add(cartProductItem.getName() +
                        " 가격변동, 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");

                } else if (isPriceChange) {
                    tempMessage.add(cartProductItem.getName() +
                        " 가격이 변동되었습니다.");

                } else if (isCountNotEnough) {
                    tempMessage.add(cartProductItem.getName() +
                        " 수량이 부족하여 구매 가능한 최대치로 변경되었습니다.");
                }
            }

            if(cartProduct.getProductItems().size() == 0){
                cart.getProducts().remove(cartProduct);
                i--;

                cart.addMessage(cartProduct.getName() + " 상품의 옵션이 모두 없어져 구매가 불가능합니다.");

            } else if(tempMessage.size() > 0){
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(cartProduct.getName() + " 상품의 변동 사항 : ");

                for(String message : tempMessage){
                    stringBuilder.append(message);
                    stringBuilder.append(", ");
                }
                cart.addMessage(stringBuilder.toString());
            }
        }

        return cart;
    }
}
