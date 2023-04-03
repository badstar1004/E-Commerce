package com.zerobase.order.application;

import static com.zerobase.order.exception.ErrorCode.ORDER_FAIL_CHECK_CART;
import static com.zerobase.order.exception.ErrorCode.ORDER_FAIL_NO_MONEY;

import com.zerobase.order.client.UserClient;
import com.zerobase.order.client.user.ChangeBalanceForm;
import com.zerobase.order.client.user.CustomerDto;
import com.zerobase.order.domain.model.ProductItem;
import com.zerobase.order.domain.redis.Cart;
import com.zerobase.order.exception.CustomException;
import com.zerobase.order.service.ProductItemService;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderApplication {

    // 결제를 위해 필요한 것
    // 1. 상품들이 전부 주문 가능한 상태인지 확인
    // 2. 가격 변동이 있는지
    // 3. 고객의 돈이 충분한지
    // 4. 결제 & 상품의 재고 관리

    private final CartApplication cartApplication;

    private final UserClient userClient;

    private final ProductItemService productItemService;

    /**
     * 주문 결제
     * @param token
     * @param cart
     */
    @Transactional
    public void order(String token, Cart cart){
        // 1. 주문 시 기존 카드 버림
        // 2. 선택주문 : 사지 않은 상품을 살려야함
        // -- 구현해야함

        // 1. 상품들이 전부 주문 가능한 상태인지 확인
        // 2. 가격 변동이 있는지
        Cart orderCart = cartApplication.refreshCart(cart);

        if(orderCart.getMessages().size() > 0){
            throw new CustomException(ORDER_FAIL_CHECK_CART);
        }

        // 3. 고객의 돈이 충분한지
        CustomerDto customerDto = userClient.getCustomerInfo(token).getBody();


        int totalPrice = getTotalPrice(cart);
        if(customerDto.getBalance() < totalPrice){
            throw new CustomException(ORDER_FAIL_NO_MONEY);
        }
    
        // 롤백 계획에 대해 생각해야함
        userClient.changeBalance(token,
            ChangeBalanceForm.builder()
                .from("USER")
                .message("Order")
                .money(-totalPrice)
                .build());

        for(Cart.Product product : orderCart.getProducts()){
            for (Cart.ProductItem cartItem : product.getProductItems()){
                ProductItem productItem = productItemService.getProductItem(cartItem.getId());
                productItem.setCount(productItem.getCount() - cartItem.getCount());
            }
        }
    }

    /**
     * 가격 계산
     * @param cart
     * @return
     */
    private Integer getTotalPrice(Cart cart){
        
        return cart.getProducts()
            .stream().flatMapToInt(product ->
                product.getProductItems()
                    .stream().flatMapToInt(productItem ->
                        IntStream.of(productItem.getPrice() * productItem.getCount())))
            .sum();
    }
}
