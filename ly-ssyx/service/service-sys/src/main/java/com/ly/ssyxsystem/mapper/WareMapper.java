package com.ly.ssyxsystem.mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ly.ssyxsystem.model.sys.Ware;

/**
* @author myz03
* @description 针对表【ware(仓库表)】的数据库操作Mapper
* @createDate 2023-10-15 12:29:35
* @Entity com.ly.ssyxsystem.Ware
*/
public interface WareMapper extends BaseMapper<Ware> {

    int insertSelective(Ware ware);

}




