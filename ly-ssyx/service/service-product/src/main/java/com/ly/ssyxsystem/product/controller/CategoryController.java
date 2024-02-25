package com.ly.ssyxsystem.product.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.model.product.Category;
import com.ly.ssyxsystem.product.service.CategoryService;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.vo.product.CategoryQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /*url: `${api_name}/${page}/${limit}`,
      method: 'get',
      params: searchObj*/
    @ApiOperation("商品分类列表")
    @GetMapping("/{page}/{limit}")
    public Result searchObj(@PathVariable("page") Long page,
                            @PathVariable("limit") Long limit,
                            CategoryQueryVo categoryQueryVo) {
        Page<Category> pageParam = new Page<>(page, limit);
        IPage<Category> pageList = categoryService.selectPageCategory(pageParam, categoryQueryVo);
        return Result.ok(pageList);
    }

    /*url: `${api_name}/get/${id}`,
    method: 'get'*/
    @ApiOperation("获取商品分类信息")
    @GetMapping("/get/{id}")
    public Result getResultById(@PathVariable("id") Long id) {
        return Result.ok(categoryService.getById(id));
    }

    @ApiOperation(value = "新增商品分类")
    @PostMapping("save")
    public Result save(@RequestBody Category category) {
        categoryService.save(category);
        return Result.ok(null);
    }

    @ApiOperation(value = "修改商品分类")
    @PutMapping("update")
    public Result updateById(@RequestBody Category category) {
        categoryService.updateById(category);
        return Result.ok(null);
    }

    @ApiOperation(value = "删除商品分类")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        // TODO 判断有无和SKU绑定关系，没有关系才允许删除分类
        categoryService.removeById(id);
        return Result.ok(null);
    }

    @ApiOperation(value = "根据id列表删除商品分类")
    @DeleteMapping("batchRemove")
    public Result batchRemove(@RequestBody List<Long> idList) {
        categoryService.removeByIds(idList);
        return Result.ok(null);
    }

    @ApiOperation("查询所有商品分类")
    @GetMapping("findAllList")
    public Result findAllList() {
        return Result.ok(categoryService.list());
    }


}
