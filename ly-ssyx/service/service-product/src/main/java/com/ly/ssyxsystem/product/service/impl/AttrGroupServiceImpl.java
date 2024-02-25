package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.AttrGroup;
import com.ly.ssyxsystem.product.mapper.AttrGroupMapper;
import com.ly.ssyxsystem.product.service.AttrGroupService;
import com.ly.ssyxsystem.vo.product.AttrGroupQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.voms.VOMSAttribute;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【attr_group(属性分组)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupMapper, AttrGroup>
        implements AttrGroupService {

    @Override
    public IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> attrGroupPage, AttrGroupQueryVo attrGroupQueryVo) {
        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(attrGroupQueryVo.getName())) {
            queryWrapper.like(AttrGroup::getName, attrGroupQueryVo.getName());
            return baseMapper.selectPage(attrGroupPage, queryWrapper);
        }
        return baseMapper.selectPage(attrGroupPage, queryWrapper);
    }

    @Override
    public List<AttrGroup> findAllListAttrGroup() {
        LambdaQueryWrapper<AttrGroup> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(AttrGroup::getId);
        return baseMapper.selectList(queryWrapper);
    }
}




