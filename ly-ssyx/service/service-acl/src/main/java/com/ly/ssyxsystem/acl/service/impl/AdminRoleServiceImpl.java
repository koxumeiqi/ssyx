package com.ly.ssyxsystem.acl.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.acl.mapper.AdminRoleMapper;
import com.ly.ssyxsystem.acl.service.AdminRoleService;
import com.ly.ssyxsystem.model.acl.AdminRole;
import org.springframework.stereotype.Service;

@Service
public class AdminRoleServiceImpl extends ServiceImpl<AdminRoleMapper, AdminRole> implements AdminRoleService {
}
