package com.ly.ssyxsystem.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.sys.Region;

import java.util.List;

/**
* @author myz03
* @description 针对表【region(地区表)】的数据库操作Service
* @createDate 2023-10-15 12:29:35
*/
public interface RegionService extends IService<Region> {

    List<Region> getRegionByKeyword(String keyword);

}
