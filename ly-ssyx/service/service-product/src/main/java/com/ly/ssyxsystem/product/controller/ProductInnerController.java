package com.ly.ssyxsystem.product.controller;


import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.product.service.CategoryService;
import com.ly.ssyxsystem.product.service.SkuInfoService;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import com.ly.ssyxsystem.vo.product.SkuStockLockVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api111/product")
public class ProductInnerController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SkuInfoService skuInfoService;

    // 根据 skuid 获取分类信息
    @GetMapping("/inner/getCategory/{categoryId}")
    public Category getCategory(@PathVariable("categoryId") Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return category;
    }

    // 根据 skuid 获取sku信息
    @GetMapping("/inner/getSkuInfo/{skuId}")
    public SkuInfo getSkuInfo(@PathVariable Long skuId) {
        return skuInfoService.getById(skuId);
    }

    @PostMapping("/inner/findCategoryList")
    public List<Category> findCategoryList(@RequestBody List<Long> categoryIds) {
        List<Category> categoryList = categoryService.listByIds(categoryIds);
        return categoryList;
    }

    // 获取所有的分类
    @PostMapping("/inner/findAllCategory")
    public List<Category> findAllCategory() {
        return categoryService.list();
    }

    // 根据关键字匹配sku列表
    @GetMapping("/inner/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable String keyword) {
        return skuInfoService.findSkuInfoByKeyword(keyword);
    }

    // 获取新人专享商品
    @GetMapping("/inner/findNewPersonSkuInfoList")
    public List<SkuInfo> findNewPersonSkuInfoList() {
        return skuInfoService.findNewPersonSkuInfoList();
    }

    @GetMapping("inner/getSkuInfoVo/{skuId}")
    public SkuInfoVo getSkuInfoVo(@PathVariable Long skuId) {
        return skuInfoService.getSkuInfoVo(skuId);
    }

    //验证和锁定库存
    @ApiOperation(value = "锁定库存")
    @PostMapping("inner/checkAndLock/{orderNo}")
    public Boolean checkAndLock(@RequestBody List<SkuStockLockVo> skuStockLockVoList,
                                @PathVariable String orderNo) {
        return skuInfoService.checkAndLock(skuStockLockVoList,orderNo);
    }

}
