package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.SkuPoster;
import com.ly.ssyxsystem.product.mapper.SkuPosterMapper;
import com.ly.ssyxsystem.product.service.SkuPosterService;
import io.jsonwebtoken.lang.Collections;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【sku_poster(商品海报表)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class SkuPosterServiceImpl extends ServiceImpl<SkuPosterMapper, SkuPoster>
        implements SkuPosterService {

    @Override
    public void updatePosterInfo(List<SkuPoster> skuPosterList, Long id) {
        LambdaQueryWrapper<SkuPoster> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuPoster::getSkuId, id);
        // 1. 先删除原来的海报
        baseMapper.delete(queryWrapper);
        // 2. 添加新的海报
        if (!Collections.isEmpty(skuPosterList)) {
            skuPosterList.forEach(skuPoster -> skuPoster.setSkuId(id));
            this.saveBatch(skuPosterList);
        }
    }

    //根据id查询商品海报列表
    @Override
    public List<SkuPoster> getPosterListBySkuId(Long id) {
        LambdaQueryWrapper<SkuPoster> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPoster::getSkuId, id);
        return baseMapper.selectList(wrapper);
    }
}




