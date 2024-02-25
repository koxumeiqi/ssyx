package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.SkuImage;
import com.ly.ssyxsystem.product.mapper.SkuImageMapper;
import com.ly.ssyxsystem.product.service.SkuImageService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【sku_image(商品图片)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class SkuImageServiceImpl extends ServiceImpl<SkuImageMapper, SkuImage>
        implements SkuImageService {

    @Override
    public void updateImageInfo(List<SkuImage> skuImagesList, Long id) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SkuImage::getSkuId, id);
        // 1. 先删除原来的图片
        baseMapper.delete(queryWrapper);
        // 2. 再添加新的图片
        if (!Collections.isEmpty(skuImagesList)) {
            skuImagesList.forEach(skuImage -> skuImage.setSkuId(id));
            this.saveBatch(skuImagesList);
        }
    }

    @Override
    public List<SkuImage> getImageListBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuImage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImage::getSkuId, skuId);
        return baseMapper.selectList(queryWrapper);
    }
}




