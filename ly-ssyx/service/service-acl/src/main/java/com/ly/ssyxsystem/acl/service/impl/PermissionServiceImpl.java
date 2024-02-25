package com.ly.ssyxsystem.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.acl.mapper.PermissionMapper;
import com.ly.ssyxsystem.acl.service.PermissionService;
import com.ly.ssyxsystem.acl.service.RolePermissionService;
import com.ly.ssyxsystem.acl.utils.PermissionHelper;
import com.ly.ssyxsystem.model.acl.Permission;
import com.ly.ssyxsystem.model.acl.RolePermission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    RolePermissionService rolePermissionService;

    @Override
    public void removeChildById(Long id) {
        List<Long> deleteIds = new ArrayList<>();
        deleteIds.add(id);

        // 递归去搜索其子id
        List<Long> searchIds = new ArrayList<>();
        searchIds.add(id);
        while (searchIds.size() > 0) {
            LambdaQueryWrapper<Permission> queryWrapper = new LambdaQueryWrapper();
            queryWrapper.in(Permission::getPid, searchIds).select(Permission::getId);
            List<Permission> permissions = baseMapper.selectList(queryWrapper);
            searchIds.clear();
            permissions.forEach(permission -> {
                searchIds.add(permission.getId());
                deleteIds.add(permission.getId());
            });
        }
        baseMapper.deleteBatchIds(deleteIds);
    }

    @Override
    public List<Permission> queryAllPermission() {
        //1 查询所有菜单
        List<Permission> allPermissionList =
                baseMapper.selectList(null);

        //2 转换要求数据格式
        List<Permission> result = PermissionHelper.buildPermission(allPermissionList);
        return result;
    }

    @Override
    @Transactional
    public void doAssign(Long roleId, Long[] permissionId) {
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionService.remove(queryWrapper);

        List<RolePermission> rolePermissions = new ArrayList<>();
        for (Long id : permissionId) {
            RolePermission rolePermission = new RolePermission();
            rolePermission.setRoleId(roleId);
            rolePermission.setPermissionId(id);
            rolePermissions.add(rolePermission);
        }
        rolePermissionService.saveBatch(rolePermissions);
    }


    @Override
    @Transactional
    public List<Permission> toAssign(Long roleId) {
        LambdaQueryWrapper<RolePermission> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RolePermission::getRoleId, roleId).select(RolePermission::getPermissionId);
        List<RolePermission> rolePermissions = rolePermissionService.list(queryWrapper);
        // 获取权限列表
        List<Long> permissionIds = new ArrayList<>();
        rolePermissions.forEach(rolePermission -> permissionIds.add(rolePermission.getPermissionId()));
        LambdaQueryWrapper<Permission> queryWrapper2 = new LambdaQueryWrapper<>();
        if (permissionIds.size() > 0)
            queryWrapper2.in(Permission::getId, permissionIds);
        List<Permission> permissions = baseMapper.selectList(queryWrapper2);
        return permissions;
    }
}
