package com.ly.ssyxsystem.home.service.impl;

import com.ly.ssyxsystem.activity.client.ActivityFeignClient;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.client.search.SearchFeignClient;
import com.ly.ssyxsystem.home.service.ItemService;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private ActivityFeignClient activityFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;
    @Resource
    private ThreadPoolExecutor threadPoolExecutor;


    //详情
    @Override
    public Map<String, Object> item(Long skuId, Long userId) {
        Map<String, Object> result = new HashMap<>();

        //skuId查询
        CompletableFuture<SkuInfoVo> skuInfocompletableFuture =
                CompletableFuture.supplyAsync(() -> {
                    //远程调用获取sku对应数据
                    SkuInfoVo skuInfoVo = productFeignClient.getSkuInfoVo(skuId);
                    result.put("skuInfoVo", skuInfoVo);
                    return skuInfoVo;
                }, threadPoolExecutor);

        //sku对应优惠卷信息
        CompletableFuture<Void> activityCompletableFuture = CompletableFuture.runAsync(() -> {
            //远程调用获取优惠卷
            Map<String, Object> activityMap =
                    activityFeignClient.findActivityAndCoupon(skuId, userId);
            result.putAll(activityMap);
        }, threadPoolExecutor);

        //更新商品热度
        CompletableFuture<Void> hotCompletableFuture = CompletableFuture.runAsync(() -> {
            //远程调用更新热度
            searchFeignClient.incrHotScore(skuId);
        }, threadPoolExecutor);

        //任务组合
        CompletableFuture.allOf(
                skuInfocompletableFuture,
                activityCompletableFuture,
                hotCompletableFuture
        ).join();
        return result;
    }
}
