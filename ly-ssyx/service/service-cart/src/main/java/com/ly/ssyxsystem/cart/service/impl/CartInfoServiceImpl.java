package com.ly.ssyxsystem.cart.service.impl;

import com.ly.ssyxsystem.cart.service.CartInfoService;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.constant.RedisConst;
import com.ly.ssyxsystem.enums.SkuType;
import com.ly.ssyxsystem.exception.SsyxException;
import com.ly.ssyxsystem.model.order.CartInfo;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import io.netty.util.HashingStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class CartInfoServiceImpl implements CartInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ProductFeignClient productFeignClient;

    private String getCartKey(Long userId) {
        return RedisConst.USER_KEY_PREFIX + userId + RedisConst.USER_CART_KEY_SUFFIX;
    }

    @Override
    public void addToCart(Long userId, Long skuId, Integer skuNum) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> operations =
                redisTemplate.boundHashOps(cartKey);

        CartInfo cartInfo = new CartInfo();
        // 判断是否有skuId
        if (operations.hasKey(skuId.toString())) {
            cartInfo = operations.get(skuId.toString());
            Integer currentSkuNum = cartInfo.getSkuNum() + skuNum;
            if (currentSkuNum < 1) {
                return;
            }
            // 更新cartInfo对象
            cartInfo.setSkuNum(currentSkuNum);
            cartInfo.setCurrentBuyNum(currentSkuNum);
            // 判断商品的数量不能大于限购数量
            Integer perLimit = cartInfo.getPerLimit();
            if (currentSkuNum > perLimit) {
                throw new SsyxException(ResultCodeEnum.SKU_LIMIT_ERROR);
            }
            cartInfo.setIsChecked(1);
            cartInfo.setUpdateTime(new Date());
        } else {
            //4 如果结果里面没有skuId，就是第一次添加
            //4.1 直接添加
            skuNum = 1;

            //远程调用根据skuId获取skuInfo
            SkuInfo skuInfo = productFeignClient.getSkuInfo(skuId);
            if (skuInfo == null) {
                throw new SsyxException(ResultCodeEnum.DATA_ERROR);
            }
            //封装cartInfo对象
            cartInfo = new CartInfo();
            cartInfo.setSkuId(skuId);
            cartInfo.setCategoryId(skuInfo.getCategoryId());
            cartInfo.setSkuType(skuInfo.getSkuType());
            cartInfo.setIsNewPerson(skuInfo.getIsNewPerson());
            cartInfo.setUserId(userId);
            cartInfo.setCartPrice(skuInfo.getPrice());
            cartInfo.setSkuNum(skuNum);
            cartInfo.setCurrentBuyNum(skuNum);
            cartInfo.setSkuType(SkuType.COMMON.getCode());
            cartInfo.setPerLimit(skuInfo.getPerLimit());
            cartInfo.setImgUrl(skuInfo.getImgUrl());
            cartInfo.setSkuName(skuInfo.getSkuName());
            cartInfo.setWareId(skuInfo.getWareId());
            cartInfo.setIsChecked(1);
            cartInfo.setStatus(1);
            cartInfo.setCreateTime(new Date());
            cartInfo.setUpdateTime(new Date());
        }
        operations.put(skuId.toString(), cartInfo);

        this.setCartKeyExpire(cartKey);
    }

    @Override
    public void deleteCart(Long skuId, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo>
                operations = redisTemplate.boundHashOps(cartKey);
        if (operations.hasKey(skuId.toString())) {
            operations.delete(skuId.toString());
        }
    }

    @Override
    public void deleteAllCart(Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo>
                operations = redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = operations.values();
        for (CartInfo cartInfo : cartInfoList) {
            operations.delete(cartInfo.getSkuId().toString());
        }
    }

    @Override
    public void batchDeleteCart(List<Long> skuIdList, Long userId) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo>
                operations = redisTemplate.boundHashOps(cartKey);
        for (Long skuId : skuIdList) {
            if (operations.hasKey(skuId)) {
                operations.delete(skuId.toString());
            }
        }
    }

    //购物车列表
    @Override
    public List<CartInfo> getCartList(Long userId) {
        //判断userId
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (StringUtils.isEmpty(userId)) {
            return cartInfoList;
        }
        //从redis获取购物车数据
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        cartInfoList = boundHashOperations.values();
        if (!CollectionUtils.isEmpty(cartInfoList)) {
            //根据商品添加时间，降序
            cartInfoList.sort(new Comparator<CartInfo>() {
                @Override
                public int compare(CartInfo o1, CartInfo o2) {
                    return o1.getCreateTime().compareTo(o2.getCreateTime());
                }
            });
        }
        return cartInfoList;
    }

    @Override
    public void checkCart(Long userId, Long skuId, Integer isChecked) {
        //获取redis的key
        String cartKey = this.getCartKey(userId);
        //cartKey获取field-value
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        //根据field（skuId）获取value（CartInfo）
        CartInfo cartInfo = boundHashOperations.get(skuId.toString());
        if (cartInfo != null) {
            cartInfo.setIsChecked(isChecked);
            //更新
            boundHashOperations.put(skuId.toString(), cartInfo);
            //设置key过期时间
            this.setCartKeyExpire(cartKey);
        }
    }

    @Override
    public void checkAllCart(Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        List<CartInfo> cartInfoList = boundHashOperations.values();
        cartInfoList.forEach(cartInfo -> {
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
        });
        this.setCartKeyExpire(cartKey);
    }

    @Override
    public void batchCheckCart(List<Long> skuIdList, Long userId, Integer isChecked) {
        String cartKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> boundHashOperations =
                redisTemplate.boundHashOps(cartKey);
        skuIdList.forEach(skuId -> {
            CartInfo cartInfo = boundHashOperations.get(skuId.toString());
            cartInfo.setIsChecked(isChecked);
            boundHashOperations.put(cartInfo.getSkuId().toString(), cartInfo);
        });
        this.setCartKeyExpire(cartKey);
    }

    @Override
    public List<CartInfo> getCartCheckedList(Long userId) {
        String cacheKey = this.getCartKey(userId);
        BoundHashOperations<String, String, CartInfo> operations =
                redisTemplate.boundHashOps(cacheKey);
        List<CartInfo> cartInfoList = operations.values();
        return cartInfoList.stream().filter(
                cartInfo -> cartInfo.getIsChecked() == 1
        ).collect(Collectors.toList());
    }

    @Override
    public void deleteCartChecked(Long userId) {
        List<CartInfo> cartInfoList = this.getCartCheckedList(userId);
        List<Long> skuIds = cartInfoList.stream().map(CartInfo::getSkuId).collect(Collectors.toList());
        // 构建redis的key值
        // hash类型 key filed-value
        String cartKey = this.getCartKey(userId);

        //根据key查询filed-value结构
        BoundHashOperations<String, String, CartInfo> hashOperations =
                redisTemplate.boundHashOps(cartKey);

        skuIds.forEach(
                skuId ->
                        hashOperations.delete(skuId.toString())
        );
    }

    //设置key 过期时间
    private void setCartKeyExpire(String key) {
        redisTemplate.expire(key, RedisConst.USER_CART_EXPIRE, TimeUnit.SECONDS);
    }
}
