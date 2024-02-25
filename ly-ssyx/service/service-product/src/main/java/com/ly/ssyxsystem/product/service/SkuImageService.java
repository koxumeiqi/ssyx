package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.SkuImage;

import java.util.List;

/**
* @author myz03
* @description 针对表【sku_image(商品图片)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface SkuImageService extends IService<SkuImage> {

    void updateImageInfo(List<SkuImage> skuImagesList, Long id);

    List<SkuImage> getImageListBySkuId(Long skuId);
}
