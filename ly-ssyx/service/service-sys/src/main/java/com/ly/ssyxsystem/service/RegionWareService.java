package com.ly.ssyxsystem.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.sys.RegionWare;
import com.ly.ssyxsystem.vo.sys.RegionWareQueryVo;

/**
* @author myz03
* @description 针对表【region_ware(城市仓库关联表)】的数据库操作Service
* @createDate 2023-10-15 12:29:35
*/
public interface RegionWareService extends IService<RegionWare> {

    IPage<RegionWare> selectPageRegionWare(IPage<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo);

    void saveRegionWare(RegionWare regionWare);

    void updateStatus(Long id, Integer status);
}
