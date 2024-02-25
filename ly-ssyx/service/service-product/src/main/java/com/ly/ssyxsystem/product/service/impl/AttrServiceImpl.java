package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.Attr;
import com.ly.ssyxsystem.product.mapper.AttrMapper;
import com.ly.ssyxsystem.product.service.AttrService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author myz03
 * @description 针对表【attr(商品属性)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class AttrServiceImpl extends ServiceImpl<AttrMapper, Attr>
        implements AttrService {

    @Override
    public List<Attr> getAttrListByGroupId(Long groupId) {
        LambdaQueryWrapper<Attr> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Attr::getAttrGroupId, groupId);
        return baseMapper.selectList(queryWrapper);
    }
}




