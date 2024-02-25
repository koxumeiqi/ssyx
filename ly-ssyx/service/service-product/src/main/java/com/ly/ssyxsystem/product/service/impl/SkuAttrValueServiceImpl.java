package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.SkuAttrValue;
import com.ly.ssyxsystem.product.mapper.SkuAttrValueMapper;
import com.ly.ssyxsystem.product.service.SkuAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【sku_attr_value(spu属性值)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class SkuAttrValueServiceImpl extends ServiceImpl<SkuAttrValueMapper, SkuAttrValue>
        implements SkuAttrValueService {

    @Override
    public void updateAttrValueInfo(List<SkuAttrValue> skuAttrValueList, Long id) {
        LambdaQueryWrapper<SkuAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuAttrValue::getSkuId, id);
        baseMapper.delete(wrapper);
        if (!skuAttrValueList.isEmpty()) {
            skuAttrValueList.forEach(skuAttrValue ->
                    skuAttrValue.setSkuId(id));
            this.saveBatch(skuAttrValueList);
        }
    }

    //根据id查询商品属性信息列表
    @Override
    public List<SkuAttrValue> getAttrValueListBySkuId(Long id) {
        LambdaQueryWrapper<SkuAttrValue> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuAttrValue::getSkuId, id);
        return baseMapper.selectList(wrapper);
    }
}




