package com.ly.ssyxsystem.activity.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.activity.CouponInfo;
import com.ly.ssyxsystem.model.order.CartInfo;
import com.ly.ssyxsystem.vo.activity.CouponRuleVo;

import java.util.List;
import java.util.Map;

/**
* @author myz03
* @description 针对表【coupon_info(优惠券信息)】的数据库操作Service
* @createDate 2023-12-10 22:26:48
*/
public interface CouponInfoService extends IService<CouponInfo> {

    IPage<CouponInfo> selectPageCouponInfo(Long page, Long limit);

    Map<String, Object> findCouponRuleList(Long id);

    void saveCouponRule(CouponRuleVo couponRuleVo);

    void deleteById(Long id);

    List<CouponInfo> findCouponInfoList(Long skuId, Long userId);

    List<CouponInfo> findCartCouponInfo(List<CartInfo> cartInfoList, Long userId);

    CouponInfo findRangeSkuIdList(List<CartInfo> cartInfoList, Long couponId);

    void updateCouponInfoUseStatus(Long couponId, Long userId, Long orderId);
}
