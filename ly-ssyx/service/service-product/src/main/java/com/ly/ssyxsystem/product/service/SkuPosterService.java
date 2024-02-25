package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.SkuPoster;

import java.util.List;

/**
* @author myz03
* @description 针对表【sku_poster(商品海报表)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface SkuPosterService extends IService<SkuPoster> {

    void updatePosterInfo(List<SkuPoster> skuPosterList, Long id);

    List<SkuPoster> getPosterListBySkuId(Long skuId);
}
