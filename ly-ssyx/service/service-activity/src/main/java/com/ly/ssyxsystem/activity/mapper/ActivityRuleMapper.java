package com.ly.ssyxsystem.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.activity.ActivityRule;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

/**
 * @author myz03
 * @description 针对表【activity_rule(优惠规则)】的数据库操作Mapper
 * @createDate 2023-12-10 22:26:48
 * @Entity com.ly.ssyxsystem.activity.ActivityRule
 */
@Mapper
public interface ActivityRuleMapper extends BaseMapper<ActivityRule> {

}




