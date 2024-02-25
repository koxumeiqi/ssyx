package com.ly.ssyxsystem.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.acl.mapper.RoleMapper;
import com.ly.ssyxsystem.acl.service.AdminRoleService;
import com.ly.ssyxsystem.acl.service.RoleService;
import com.ly.ssyxsystem.model.acl.AdminRole;
import com.ly.ssyxsystem.model.acl.Role;
import com.ly.ssyxsystem.vo.acl.RoleQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    AdminRoleService adminRoleService;

    //1 角色列表（条件分页查询）
    @Override
    public IPage<Role> selectRolePage(Page<Role> pageParam,
                                      RoleQueryVo roleQueryVo) {
        //获取条件值
        String roleName = roleQueryVo.getRoleName();

        //创建mp条件对象
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();

        //判断条件值是否为空，不为封装查询条件
        // rolename like ?
        if (!StringUtils.isEmpty(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }

        //调用方法实现条件分页查询
        IPage<Role> rolePage = baseMapper.selectPage(pageParam, wrapper);

        //返回分页对象
        return rolePage;
    }

    @Override
    public Map<String, Object> getRoleByAdminId(Long adminId) {

        //获取所有角色，和根据用户id查询用户分配角色列表
        Map<String, Object> ans = new HashMap<>();
        // 查询所有角色
        List<Role> roles = baseMapper.selectList(null);
        ans.put("allRolesList", roles);
        // 根据用户id查询角色
        LambdaQueryWrapper<AdminRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AdminRole::getAdminId, adminId).select(AdminRole::getRoleId);
        List<AdminRole> adminRoles = adminRoleService.list(queryWrapper);
        List<Long> roleIds = adminRoles.stream()
                .map(AdminRole::getRoleId)
                .collect(Collectors.toList());
        List<Role> roleList = new ArrayList<>();
        roleIds.stream().forEach(roleId -> roleList.add(baseMapper.selectById(roleId)));
        ans.put("assignRoles", roleList);
        return ans;
    }

    //为用户进行分配
    @Override
    public void saveAdminRole(Long adminId, Long[] roleIds) {
        //1 删除用户已经分配过的角色数据
        //根据用户id删除admin_role表里面对应数据
        LambdaQueryWrapper<AdminRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AdminRole::getAdminId, adminId);
        adminRoleService.remove(wrapper);

        //2 重新分配
        //adminId:1   roleId: 2 3
        //遍历多个角色id，得到每个角色id，拿着每个角色id + 用户id添加用户角色关系表
//        for (Long roleId:roleIds) {
//            AdminRole adminRole = new AdminRole();
//            adminRole.setAdminId(adminId);
//            adminRole.setRoleId(roleId);
//            adminRoleService.save(adminRole);
//        }
        List<AdminRole> list = new ArrayList<>();
        for (Long roleId : roleIds) {
            AdminRole adminRole = new AdminRole();
            adminRole.setAdminId(adminId);
            adminRole.setRoleId(roleId);
            //放到list集合
            list.add(adminRole);
        }
        //调用方法添加
        adminRoleService.saveBatch(list);
    }


}
