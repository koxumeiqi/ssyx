package com.ly.ssyxsystem.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.product.mapper.CategoryMapper;
import com.ly.ssyxsystem.product.service.CategoryService;
import com.ly.ssyxsystem.vo.product.CategoryQueryVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author myz03
 * @description 针对表【category(商品三级分类)】的数据库操作Service实现
 * @createDate 2023-11-04 15:51:21
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
        implements CategoryService {

    @Override
    public IPage<Category> selectPageCategory(Page<Category> pageParam, CategoryQueryVo categoryQueryVo) {

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(categoryQueryVo.getName())) {
            queryWrapper.like(Category::getName, categoryQueryVo.getName());
            return baseMapper.selectPage(pageParam, queryWrapper);
        }
        return baseMapper.selectPage(pageParam, queryWrapper);
    }
}




