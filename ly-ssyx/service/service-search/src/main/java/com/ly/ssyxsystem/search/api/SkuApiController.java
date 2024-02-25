package com.ly.ssyxsystem.search.api;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.model.search.SkuEs;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.search.service.SkuService;
import com.ly.ssyxsystem.vo.search.PageVO;
import com.ly.ssyxsystem.vo.search.SkuEsQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api111/search/sku")
public class SkuApiController {

    @Autowired
    private SkuService skuService;

    // 查询分类中的商品
    @GetMapping("{page}/{limit}")
    public Result listSku(@PathVariable int page, @PathVariable int limit
            , SkuEsQueryVo skuEsQueryVo) {
        Pageable pageable = PageRequest.of(page - 1, limit);
        PageVO<SkuEs> pageModel = skuService.search(page, limit, skuEsQueryVo);
        return Result.ok(pageModel);
    }

    // 获取前十件爆款商品
    @GetMapping("inner/findHotSkuList")
    public List<SkuEs> findHotSkuList() {
        return skuService.findHotSkuList();
    }

    //上架
    @GetMapping("inner/upperSku/{skuId}")
    public Result upperSku(@PathVariable Long skuId) {
        skuService.upperSku(skuId);
        return Result.ok(null);
    }

    //下架
    @GetMapping("inner/lowerSku/{skuId}")
    public Boolean lowerSku(@PathVariable Long skuId) {
        skuService.lowerSku(skuId);
        return true;
    }

    //更新商品热度
    @GetMapping("inner/incrHotScore/{skuId}")
    public Boolean incrHotScore(@PathVariable("skuId") Long skuId) {
        skuService.incrHotScore(skuId);
        return true;
    }


}
