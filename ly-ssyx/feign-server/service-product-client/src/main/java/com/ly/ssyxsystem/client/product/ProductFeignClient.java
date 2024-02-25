package com.ly.ssyxsystem.client.product;


import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import com.ly.ssyxsystem.vo.product.SkuStockLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "service-product")
public interface ProductFeignClient {

    //根据skuId获取sku信息
    @GetMapping("/api111/product/inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId);

    // 获取新人专享商品
    @GetMapping("/api111/product/inner/findNewPersonSkuInfoList")
    List<SkuInfo> findNewPersonSkuInfoList();

    /*如果是请求体传参，得把 @RequestBody 注解删除*/

    // 根据 skuid 获取分类信息
    @GetMapping("/api111/product/inner/getCategory/{categoryId}")
    Category getCategory(@PathVariable("categoryId") Long categoryId);

    // 根据 skuid 获取sku信息
    @GetMapping("/api111/product/inner/getSkuInfo/{skuId}")
    SkuInfo getSkuInfo(@PathVariable("skuId") Long skuId);

    @PostMapping("/admin/product/skuInfo/get")
    List<SkuInfo> getSkuInfos(@RequestBody List<Long> ids);

    @PostMapping("/api111/product/inner/findCategoryList")
    List<Category> findCategoryList(@RequestBody List<Long> categoryIds);

    @GetMapping("/api111/product/inner/findSkuInfoByKeyword/{keyword}")
    List<SkuInfo> findSkuInfoByKeyword(@PathVariable String keyword);

    // 获取所有的分类
    @PostMapping("/api111/product/inner/findAllCategory")
    List<Category> findAllCategory();

    //验证和锁定库存
    @PostMapping("/api111/product/inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable("orderNo") String orderNo);
}
