package com.ecommerce.cart.repository;

import com.ecommerce.cart.domain.entity.Cart;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Repository
public class CartRedisRepository implements CartRepository{
    private final RedisTemplate<String,Object> redisTemplate;

    @Value("${cart.ttl-days:7}")
    private long timeBeforeCartToExpire;
    private static final String CART_PREFIX= "cart:";

    @Override
    public Cart save(Cart cart) {
        String key = CART_PREFIX + cart.getCartId();
        redisTemplate.opsForValue().set(key,cart, Duration.ofDays(timeBeforeCartToExpire));
        log.debug("Cart saved to Redis: {} with TTL expiration  {} days", cart.getCartId(), timeBeforeCartToExpire);
        return cart;    }

    @Override
    public Optional<Cart> findById(String cartId) {
        String key = CART_PREFIX + cartId;
        Cart cart =(Cart) redisTemplate.opsForValue().get(key);
        log.debug("Cart rretrieved from Redis: {}",cartId);
        return Optional.ofNullable(cart);
    }

    @Override
    public void deletedById(String cartId) {
        String key = CART_PREFIX + cartId;
        redisTemplate.delete(key);
        log.debug("Cart deleted from Redis: {}",key);
    }

    @Override
    public boolean existsById(String cartId) {
        String key = CART_PREFIX + cartId;
        Boolean exists = redisTemplate.hasKey(key);

        if (exists == null) {
            log.warn("Impossible to verify the existence of the cart {} (Redis respond null)", cartId);
            return false;
        }

        if (exists) {
            return true;
        } else {
            return false;
        }
    }
}
