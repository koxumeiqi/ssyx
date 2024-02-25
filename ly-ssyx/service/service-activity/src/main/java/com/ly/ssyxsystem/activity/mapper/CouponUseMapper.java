package com.ly.ssyxsystem.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.activity.CouponUse;
import org.apache.ibatis.annotations.Mapper;

/**
* @author myz03
* @description 针对表【coupon_use(优惠券领用表)】的数据库操作Mapper
* @createDate 2023-12-10 22:26:48
* @Entity com.ly.ssyxsystem.activity.CouponUse
*/
@Mapper
public interface CouponUseMapper extends BaseMapper<CouponUse> {

}




