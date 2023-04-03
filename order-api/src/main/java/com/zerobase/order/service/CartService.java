package com.zerobase.order.service;

import com.zerobase.order.client.RedisClient;
import com.zerobase.order.domain.product.AddProductCartForm;
import com.zerobase.order.domain.redis.Cart;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final RedisClient redisClient;

    public Cart getCart(Long customerId){
        Cart cart = redisClient.get(customerId, Cart.class);
        return (cart != null ? cart : new Cart());
    }


    public Cart putCart(Long customerId, Cart cart){
        redisClient.put(customerId, cart);
        return cart;
    }

    /**
     * 장바구니 추가
     * @param customerId
     * @param addProductCartForm
     * @return
     */
    public Cart addCart(Long customerId, AddProductCartForm addProductCartForm){
        Cart cart = redisClient.get(customerId, Cart.class);

        if(cart == null){
            cart = new Cart();
            cart.setCustomerId(customerId);
        }

        // 이전 같은 상품 유무
        Optional<Cart.Product> optionalProduct =
            cart.getProducts().stream()
                .filter(product -> product.getId().equals(addProductCartForm.getId()))
                .findFirst();

        // 같은 상품이 있다면
        if(optionalProduct.isPresent()){
            Cart.Product redisProduct = optionalProduct.get();

            // 요청한 상품 아이템
            List<Cart.ProductItem> productItems =
                addProductCartForm.getProductItems()
                    .stream().map(Cart.ProductItem::from).collect(Collectors.toList());

            // redis 에 올려진 상품 아이템
            Map<Long, Cart.ProductItem> redisItemMap =
                redisProduct.getProductItems()
                    .stream().collect(Collectors.toMap(it -> it.getId(), it -> it));

            if(!redisProduct.getName().equals(addProductCartForm.getName())){
                cart.addMessage(redisProduct.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }

            for(Cart.ProductItem item : productItems){
                Cart.ProductItem redisItem = redisItemMap.get(item.getId());

                // 올려진 상품이 없다면
                if(redisItem == null){
                    // 추가
                    redisProduct.getProductItems().add(item);
                } else {
                    // 올려진 상품이 있다면
                    // 가격 변동 체크
                    if(!redisItem.getPrice().equals(item.getPrice())){
                        cart.addMessage(
                            redisProduct.getName() + item.getName() + "의 가격이 변경되었습니다. 확인 부탁드립니다.");
                    }
                    // 수량 추가
                    redisItem.setCount(redisItem.getCount() + item.getCount());
                }
            }
            // 같은 상품이 없다면
        } else {
            Cart.Product product = Cart.Product.from(addProductCartForm);
            cart.getProducts().add(product);
        }

        redisClient.put(customerId, cart);
        return cart;
    }
}
