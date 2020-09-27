package com.leyo.cart.service;

import com.leyo.cart.client.GoodsClient;
import com.leyo.cart.interceptor.LoginInterceptor;
import com.leyo.cart.pojo.Cart;
import com.leyou.auth.entity.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private GoodsClient goodsClient;


    private static final String KEY_PREFIX = "leyou:cart:uid:";

    private static final Logger LOGGER = LoggerFactory.getLogger(CartService.class);


    public void addCart(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();

        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        Integer num = cart.getNum();
        Long skuId = cart.getSkuId();

        Boolean aBoolean = hashOps.hasKey(skuId.toString());
        if (aBoolean) {

            System.out.println(hashOps.get(skuId.toString()));

            String json = hashOps.get(skuId.toString()).toString();
            Cart newCart = JsonUtils.parse(json, Cart.class);
            newCart.setNum(cart.getNum() + newCart.getNum());

            hashOps.put(newCart.getSkuId().toString(), JsonUtils.serialize(newCart));
        } else {
            //不存在，则添加数据
            cart.setUserId(userInfo.getId());

            Sku sku = this.goodsClient.querySkuById(skuId);

            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setPrice(sku.getPrice());
            cart.setSkuId(skuId);
            cart.setTitle(sku.getTitle());

            hashOps.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));
        }
    }

    public List<Cart> queryCart() {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();
        //没有key直接返回
        if (!redisTemplate.hasKey(key)) {
            return null;
        }

        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        List<Object> values = hashOps.values();

        //没有数据直接返回null
        if (CollectionUtils.isEmpty(values)) {
            return null;
        }

        return values.stream().map(o -> JsonUtils.parse(o.toString(),Cart.class)).collect(Collectors.toList());




    }

    /**
     * 编辑商品数量
     * @param cart
     */
    public void editCart(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        String json = hashOps.get(skuId.toString()).toString();
        Cart getCart = JsonUtils.parse(json, Cart.class);

        getCart.setNum(num);

        hashOps.put(skuId.toString(), JsonUtils.serialize(getCart));
    }

    /**
     * 删除商品
     * @param id
     */
    public void deleteCart(Long id) {

        UserInfo userInfo = LoginInterceptor.getUserInfo();

        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = redisTemplate.boundHashOps(key);

        hashOps.delete(id.toString());



    }
}
