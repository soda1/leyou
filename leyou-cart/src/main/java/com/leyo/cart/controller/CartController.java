package com.leyo.cart.controller;

import com.leyo.cart.pojo.Cart;
import com.leyo.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     * @param cart
     * @return
     */
    @PostMapping()
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 获取购物车
     * @return
     */
    @GetMapping()
    public ResponseEntity<List<Cart>> queryCart() {
        List<Cart> carts = this.cartService.queryCart();

        if (carts == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carts);
    }

    /**
     * 更改商品数量
     * @param cart
     * @return
     */
    @PutMapping()
    public ResponseEntity<Void> editCart(@RequestBody Cart cart) {
        this.cartService.editCart(cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 删除商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

}
