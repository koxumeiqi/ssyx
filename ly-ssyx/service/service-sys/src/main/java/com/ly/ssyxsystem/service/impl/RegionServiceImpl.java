package com.ly.ssyxsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.sys.Region;
import com.ly.ssyxsystem.service.RegionService;
import com.ly.ssyxsystem.mapper.RegionMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【region(地区表)】的数据库操作Service实现
 * @createDate 2023-10-15 12:29:35
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region>
        implements RegionService {

    @Override
    public List<Region> getRegionByKeyword(String keyword) {
        LambdaQueryWrapper<Region> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Region::getName, keyword);
        return baseMapper.selectList(queryWrapper);
    }
}




