package com.ly.ssyxsystem.client.search;


import com.ly.ssyxsystem.model.search.SkuEs;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "service-search")
public interface SearchFeignClient {

    @GetMapping("/api111/search/sku/inner/lowerSku/{skuId}")
    Boolean lowerSku(@PathVariable Long skuId);

    // 获取前十件爆款商品
    @GetMapping("/api111/search/sku/inner/findHotSkuList")
    List<SkuEs> findHotSkuList();

    //更新商品热度
    @GetMapping("/api111/search/sku/inner/incrHotScore/{skuId}")
    Boolean incrHotScore(@PathVariable("skuId") Long skuId);
}
