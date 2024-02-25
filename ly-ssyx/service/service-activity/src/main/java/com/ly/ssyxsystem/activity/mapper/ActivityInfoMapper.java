package com.ly.ssyxsystem.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.activity.ActivityInfo;
import com.ly.ssyxsystem.model.activity.ActivityRule;
import com.ly.ssyxsystem.model.activity.ActivitySku;
import org.apache.ibatis.annotations.Param;
import org.junit.experimental.theories.ParametersSuppliedBy;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【activity_info(活动表)】的数据库操作Mapper
 * @createDate 2023-12-10 22:26:47
 * @Entity com.ly.ssyxsystem.activity.ActivityInfo
 */
public interface ActivityInfoMapper extends BaseMapper<ActivityInfo> {

    List<Long> selectSkuIdListExist(@Param("skuIdList") List<Long> skuIdList);

    List<ActivityRule> findActivityRule(Long skuId);

    List<ActivitySku> selectCartActivity(@Param("skuIdList") List<Long> skuIdList);
}




