package com.ly.ssyxsystem.product.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.product.SkuInfo;
import org.apache.ibatis.annotations.Param;

/**
 * @author myz03
 * @description 针对表【sku_info(sku信息)】的数据库操作Mapper
 * @createDate 2023-11-04 15:51:21
 * @Entity com.ly.ssyxsystem.SkuInfo
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    int insertAndGetId(SkuInfo skuInfo);

    // 解锁库存
    void unLockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 锁住库存
    Integer lockStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    // 验证库存
    SkuInfo checkStock(@Param("skuId") Long skuId, @Param("skuNum") Integer skuNum);

    void minusStock(@Param("skuId") Long skuId,@Param("skuNum") Integer skuNum);
}




