package com.ly.ssyxsystem.acl.controller;


import com.ly.ssyxsystem.model.acl.Permission;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.acl.service.PermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "菜单管理")
@RequestMapping("/admin/acl/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // 获取某个角色的权限列表
    @ApiOperation("获取某个角色的权限列表")
    @GetMapping("toAssign/{roleId}")
    public Result toAssign(@PathVariable Long roleId) {
        List<Permission> permissions =  permissionService.toAssign(roleId);
        return Result.ok(permissions);
    }

    // 为某个角色授权
    @ApiOperation("为某个角色授权")
    @PostMapping("doAssign")
    public Result doAssign(Long roleId, Long[] permissionId) {
        permissionService.doAssign(roleId, permissionId);
        return Result.ok();
    }

    //查询所有菜单
//    url: `${api_name}`,
//    method: 'get'
    @ApiOperation("查询所有菜单")
    @GetMapping
    public Result list() {
        List<Permission> list = permissionService.queryAllPermission();
        return Result.ok(list);
    }

    @ApiOperation("添加菜单")
    @PostMapping("save")
    public Result save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.ok(null);
    }

    @ApiOperation("修改菜单")
    @PutMapping("update")
    public Result update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.ok(null);
    }

    @ApiOperation("递归删除菜单")
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        permissionService.removeChildById(id);
        return Result.ok(null);
    }

}
