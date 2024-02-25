package com.ly.ssyxsystem.home.controller;


import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.result.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("商品分类")
@RestController
@RequestMapping("api111/home")
public class CategoryApiController {


    @Autowired
    private ProductFeignClient productFeignClient;

    // 查询所有分类
    @GetMapping("category")
    public Result categoryList() {
        List<Category> categoryList = productFeignClient.findAllCategory();
        return Result.ok(categoryList);
    }

}
