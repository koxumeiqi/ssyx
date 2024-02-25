package com.ly.ssyxsystem.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.activity.CouponInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author myz03
* @description 针对表【coupon_info(优惠券信息)】的数据库操作Mapper
* @createDate 2023-12-10 22:26:48
* @Entity com.ly.ssyxsystem.activity.CouponInfo
*/
public interface CouponInfoMapper extends BaseMapper<CouponInfo> {

    //2 根据skuId+userId查询优惠卷信息
    List<CouponInfo> selectCouponInfoList(@Param("skuId") Long id,
                                          @Param("categoryId") Long categoryId,
                                          @Param("userId") Long userId);

    List<CouponInfo> selectCartCouponInfoList(Long userId);
}




