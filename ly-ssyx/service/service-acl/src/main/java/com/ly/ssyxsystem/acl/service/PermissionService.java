package com.ly.ssyxsystem.acl.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.acl.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    void removeChildById(Long id);

    List<Permission> queryAllPermission();

    void doAssign(Long roleId, Long[] permissionId);

    List<Permission> toAssign(Long roleId);
}
