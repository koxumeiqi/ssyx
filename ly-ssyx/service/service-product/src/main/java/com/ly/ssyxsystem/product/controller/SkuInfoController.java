package com.ly.ssyxsystem.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.product.service.SkuInfoService;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.vo.product.SkuInfoQueryVo;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product/skuInfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    // sku列表
    @ApiOperation("sku列表")
    @GetMapping("/{page}/{limit}")
    public Result list(@PathVariable("page") Long page,
                       @PathVariable("limit") Long limit,
                       SkuInfoQueryVo skuInfoQueryVo) {
        Page<SkuInfo> skuInfoPage = new Page<>(page, limit);
        IPage<SkuInfo> skuInfos = skuInfoService.selectPageList(skuInfoQueryVo, skuInfoPage);
        return Result.ok(skuInfos);
    }

    /*url: `${api_name}/save`,
    method: 'post',*/
    @ApiOperation("商品SKU添加")
    @PostMapping("save")
    public Result save(@RequestBody SkuInfoVo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    @ApiOperation("获取sku信息")
    @GetMapping("get/{id}")
    public Result getSkuInfo(@PathVariable("id") Long id) {
        SkuInfoVo skuInfoVo = skuInfoService.getSkuInfo(id);
        return Result.ok(skuInfoVo);
    }

    @PostMapping("get")
    public List<SkuInfo> getSkuInfos(@RequestBody List<Long> ids) {
        return skuInfoService.listByIds(ids);
    }

    @ApiOperation("修改su")
    @PutMapping("update")
    public Result update(@RequestBody SkuInfoVo skuInfo) {
        skuInfoService.updateSkuInfo(skuInfo);
        return Result.ok();
    }

    @ApiOperation(value = "删除")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        skuInfoService.removeSkuInfo(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        skuInfoService.removeSkuInfoByIds(idList);
        return Result.ok(null);
    }

    @ApiOperation("商品审核")
    @GetMapping("check/{skuId}/{status}")
    public Result check(@PathVariable("skuId") Long skuId,
                        @PathVariable("status") Integer status) {
        skuInfoService.check(skuId, status);
        return Result.ok();
    }

    @ApiOperation("商品上下架")
    @GetMapping("publish/{skuId}/{status}")
    public Result saleStatus(@PathVariable("skuId") Long skuId,
                             @PathVariable("status") Integer status) {
        skuInfoService.publish(skuId, status);
        return Result.ok();
    }

    //新人专享
    @GetMapping("isNewPerson/{skuId}/{status}")
    public Result isNewPerson(@PathVariable("skuId") Long skuId,
                              @PathVariable("status") Integer status) {
        skuInfoService.isNewPerson(skuId, status);
        return Result.ok(null);
    }


}
