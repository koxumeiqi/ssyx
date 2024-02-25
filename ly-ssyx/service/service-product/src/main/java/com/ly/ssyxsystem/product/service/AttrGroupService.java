package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.AttrGroup;
import com.ly.ssyxsystem.vo.product.AttrGroupQueryVo;

import java.util.List;

/**
* @author myz03
* @description 针对表【attr_group(属性分组)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface AttrGroupService extends IService<AttrGroup> {

    IPage<AttrGroup> selectPageAttrGroup(Page<AttrGroup> attrGroupPage, AttrGroupQueryVo attrGroupQueryVo);

    List<AttrGroup> findAllListAttrGroup();

}
