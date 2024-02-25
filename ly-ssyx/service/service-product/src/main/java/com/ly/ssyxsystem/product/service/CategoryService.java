package com.ly.ssyxsystem.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.vo.product.CategoryQueryVo;

/**
* @author myz03
* @description 针对表【category(商品三级分类)】的数据库操作Service
* @createDate 2023-11-04 15:51:21
*/
public interface CategoryService extends IService<Category> {

    IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo);
}
