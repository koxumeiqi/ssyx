package com.ly.ssyxsystem.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.exception.SsyxException;
import com.ly.ssyxsystem.model.sys.RegionWare;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import com.ly.ssyxsystem.service.RegionWareService;
import com.ly.ssyxsystem.mapper.RegionWareMapper;
import com.ly.ssyxsystem.vo.sys.RegionWareQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author myz03
 * @description 针对表【region_ware(城市仓库关联表)】的数据库操作Service实现
 * @createDate 2023-10-15 12:29:35
 */
@Service
public class RegionWareServiceImpl extends ServiceImpl<RegionWareMapper, RegionWare>
        implements RegionWareService {

    @Override
    public IPage<RegionWare> selectPageRegionWare(IPage<RegionWare> pageParam, RegionWareQueryVo regionWareQueryVo) {

        // 1. 获取查询条件值
        String keyword = regionWareQueryVo.getKeyword();
        // 2. 封装查询条件
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(keyword)) {
            queryWrapper.like(RegionWare::getRegionName, keyword)
                    .or()
                    .like(RegionWare::getWareName, keyword);
        }
        // 3. 查询数据
        IPage<RegionWare> regionWareIPage = baseMapper.selectPage(pageParam, queryWrapper);

        return regionWareIPage;
    }

    @Override
    public void saveRegionWare(RegionWare regionWare) {
        LambdaQueryWrapper<RegionWare> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(RegionWare::getRegionId, regionWare.getRegionId());
        Integer count = baseMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new SsyxException(ResultCodeEnum.REGION_OPEN);
        }
        baseMapper.insert(regionWare);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        RegionWare regionWare = baseMapper.selectById(id);
        regionWare.setStatus(status);
        baseMapper.updateById(regionWare);
    }
}




