package com.zerobase.order.controller;

import com.zerobase.domain.config.JwtAuthenticationProvider;
import com.zerobase.order.application.CartApplication;
import com.zerobase.order.application.OrderApplication;
import com.zerobase.order.domain.product.AddProductCartForm;
import com.zerobase.order.domain.redis.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/customer/cart")
public class CustomerCartController {

    private final CartApplication cartApplication;

    private final OrderApplication orderApplication;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    /**
     * 장바구니 추가
     * @param token
     * @param addProductCartForm
     * @return
     */
    @PostMapping
    public ResponseEntity<Cart> addCart(
        @RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody AddProductCartForm addProductCartForm){
        return ResponseEntity.ok(cartApplication.addCart(
            jwtAuthenticationProvider.getUserVo(token).getId(), addProductCartForm));
    }

    /**
     * 장바구니 확인
     * @param token
     * @return
     */
    @GetMapping
    public ResponseEntity<Cart> showCart(
        @RequestHeader(name = "X-AUTO-TOKEN") String token) {
        return ResponseEntity.ok(cartApplication.getCart(
            jwtAuthenticationProvider.getUserVo(token).getId()));
    }

    /**
     * 장바구니 변경
     * @param token
     * @param cart
     * @return
                */
        @PutMapping
        public ResponseEntity<Cart> updateCart(
            @RequestHeader(name = "X-AUTO-TOKEN") String token,
            @RequestBody Cart cart){
            return ResponseEntity.ok(cartApplication.updateCart(
                jwtAuthenticationProvider.getUserVo(token).getId(), cart));
    }

    /**
     * 장바구니 주문
     * @param token
     * @param cart
     * @return
     */
    @PostMapping("/order")
    public ResponseEntity<Cart> order(
        @RequestHeader(name = "X-AUTO-TOKEN") String token,
        @RequestBody Cart cart){

        orderApplication.order(token, cart);
        return ResponseEntity.ok().build();
    }
}
