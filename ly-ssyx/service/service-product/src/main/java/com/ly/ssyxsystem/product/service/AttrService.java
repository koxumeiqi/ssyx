package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.Attr;

import java.util.List;

/**
* @author myz03
* @description 针对表【attr(商品属性)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface AttrService extends IService<Attr> {

    List<Attr> getAttrListByGroupId(Long groupId);
}
