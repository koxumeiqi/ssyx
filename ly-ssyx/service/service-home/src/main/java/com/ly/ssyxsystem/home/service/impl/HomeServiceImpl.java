package com.ly.ssyxsystem.home.service.impl;

import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.client.search.SearchFeignClient;
import com.ly.ssyxsystem.client.user.UserFeignClient;
import com.ly.ssyxsystem.home.service.HomeService;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.model.search.SkuEs;
import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
public class HomeServiceImpl implements HomeService {

    @Autowired
    private UserFeignClient userFeignClient;

    @Autowired
    private ProductFeignClient productFeignClient;

    @Autowired
    private SearchFeignClient searchFeignClient;

    @Resource
    private ThreadPoolExecutor threadPoolExecutor;

    @Override
    public Map<String, Object> homeData(Long userId) {

        if (userId == null) {
            return null;
        }
        Map<String, Object> result = new HashMap<>();
        // 1. 根据userId获取当前登录语句提货地址信息
        // 远程调用service-user模块接口获取需要数据
        CompletableFuture<LeaderAddressVo> leaderAddressCompletableFuture = CompletableFuture.supplyAsync(() -> {
            LeaderAddressVo leaderAddressVO = userFeignClient.getUserAddressByUserId(userId);
            result.put("leaderAddressVo", leaderAddressVO);
            return leaderAddressVO;
        }, threadPoolExecutor);
        /*LeaderAddressVo leaderAddressVO = userFeignClient.getUserAddressByUserId(userId);
        result.put("leaderAddressVo", leaderAddressVO);*/
        // 2. 获取所有分类
        // 远程调用 service-product 模块接口
        CompletableFuture<List<Category>> allCategoryCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<Category> allCategory = productFeignClient.findAllCategory();
            result.put("categoryList", allCategory);
            return allCategory;
        }, threadPoolExecutor);
        /*List<Category> allCategory = productFeignClient.findAllCategory();
        result.put("categoryList", allCategory);*/
        // 3. 获取新人专享商品
        // 远程调用 service-product 模块接口
        CompletableFuture<List<SkuInfo>> personSkuInfoCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<SkuInfo> personSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
            result.put("newPersonSkuInfoList", personSkuInfoList);
            return personSkuInfoList;
        }, threadPoolExecutor);
        /*List<SkuInfo> personSkuInfoList = productFeignClient.findNewPersonSkuInfoList();
        result.put("newPersonSkuInfoList", personSkuInfoList);*/
        // 4. 获取爆款商品
        // 远程调用service-search 模块接口
        // score 评分降序
        CompletableFuture<List<SkuEs>> hotSkuCompletableFuture = CompletableFuture.supplyAsync(() -> {
            List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
            result.put("hotSkuList", hotSkuList);
            return hotSkuList;
        }, threadPoolExecutor);
        /*List<SkuEs> hotSkuList = searchFeignClient.findHotSkuList();
        result.put("hotSkuList", hotSkuList);*/

        // 任务组合
        CompletableFuture.allOf(
                leaderAddressCompletableFuture,
                allCategoryCompletableFuture,
                personSkuInfoCompletableFuture,
                hotSkuCompletableFuture
        ).join();
        return result;
    }
}
