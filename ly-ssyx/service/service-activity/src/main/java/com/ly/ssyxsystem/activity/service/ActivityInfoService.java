package com.ly.ssyxsystem.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.activity.ActivityInfo;
import com.ly.ssyxsystem.model.order.CartInfo;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.vo.activity.ActivityRuleVo;
import com.ly.ssyxsystem.vo.order.CartInfoVo;
import com.ly.ssyxsystem.vo.order.OrderConfirmVo;

import java.util.List;
import java.util.Map;

/**
* @author myz03
* @description 针对表【activity_info(活动表)】的数据库操作Service
* @createDate 2023-12-10 22:26:48
*/
public interface ActivityInfoService extends IService<ActivityInfo> {

    IPage<ActivityInfo> getPageList(Page<ActivityInfo> pageParam);

    Map<String, Object> findActivityRuleList(Long id);

    void saveActivityRule(ActivityRuleVo activityRuleVo);

    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    Map<Long, List<String>> findActivity(List<Long> skuIdList);

    Map<String, Object> findActivityAndCoupon(Long skuId, Long userId);

    OrderConfirmVo findCartActivityAndCoupon(List<CartInfo> cartInfoList, Long userId);

    List<CartInfoVo> findCartActivityList(List<CartInfo> cartInfoList);

}
