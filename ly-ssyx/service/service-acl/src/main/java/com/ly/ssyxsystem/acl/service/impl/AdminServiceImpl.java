package com.ly.ssyxsystem.acl.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.acl.mapper.AdminMapper;
import com.ly.ssyxsystem.acl.service.AdminService;
import com.ly.ssyxsystem.model.acl.Admin;
import com.ly.ssyxsystem.vo.acl.AdminQueryVo;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Override
    public IPage<Admin> selectPageUser(Page<Admin> pageParam, AdminQueryVo adminQueryVo) {

        String name = adminQueryVo.getName();
        String username = adminQueryVo.getUsername();
        LambdaQueryWrapper<Admin> queryWrapper = new LambdaQueryWrapper<>();
        if (!StringUtils.isEmpty(username)) {
            queryWrapper.eq(Admin::getUsername, username);
        }
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like(Admin::getName, name);
        }
        baseMapper.selectPage(pageParam, queryWrapper);
        return pageParam;
    }

}
