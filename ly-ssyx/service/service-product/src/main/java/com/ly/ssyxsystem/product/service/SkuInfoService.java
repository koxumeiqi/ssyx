package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.vo.product.SkuInfoQueryVo;
import com.ly.ssyxsystem.vo.product.SkuInfoVo;
import com.ly.ssyxsystem.vo.product.SkuStockLockVo;

import java.util.List;

/**
* @author myz03
* @description 针对表【sku_info(sku信息)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface SkuInfoService extends IService<SkuInfo> {

    IPage<SkuInfo> selectPageList(SkuInfoQueryVo skuInfoQueryVo, Page<SkuInfo> skuInfoPage);

    void saveSkuInfo(SkuInfoVo skuInfo);

    SkuInfoVo getSkuInfo(Long id);

    void updateSkuInfo(SkuInfoVo skuInfo);

    void check(Long skuId, Integer status);

    void publish(Long skuId, Integer status);

    void isNewPerson(Long skuId, Integer status);

    void removeSkuInfo(Long id);

    void removeSkuInfoByIds(List<Long> idList);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    List<SkuInfo> findNewPersonSkuInfoList();

    SkuInfoVo getSkuInfoVo(Long skuId);

    Boolean checkAndLock(List<SkuStockLockVo> skuStockLockVoList, String orderNo);

    void minusStock(String orderNo);
}
