///*
//package com.ly.ssyxsystem.search.controller;
//
//
//import com.ly.ssyxsystem.result.Result;
//import com.ly.ssyxsystem.search.service.SkuService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("api/search/sku")
//public class SkuApiController {
//
//    @Autowired
//    private SkuService skuService;
//
//    //上架
//    @GetMapping("inner/upperSku/{skuId}")
//    public Result upperSku(@PathVariable Long skuId) {
//        skuService.upperSku(skuId);
//        return Result.ok(null);
//    }
//
//    //下架
//    @GetMapping("inner/lowerSku/{skuId}")
//    public Boolean lowerSku(@PathVariable Long skuId) {
//        skuService.lowerSku(skuId);
//        return true;
//    }
//
//}
//*/
