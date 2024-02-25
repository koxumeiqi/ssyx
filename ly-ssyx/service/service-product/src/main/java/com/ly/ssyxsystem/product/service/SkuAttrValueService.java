package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.SkuAttrValue;

import java.util.List;

/**
* @author myz03
* @description 针对表【sku_attr_value(spu属性值)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface SkuAttrValueService extends IService<SkuAttrValue> {

    void updateAttrValueInfo(List<SkuAttrValue> skuAttrValueList, Long id);

    List<SkuAttrValue> getAttrValueListBySkuId(Long skuId);
}
