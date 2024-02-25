package com.ly.ssyxsystem.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.enums.user.Leader;
import com.ly.ssyxsystem.enums.user.User;
import com.ly.ssyxsystem.enums.user.UserDelivery;
import com.ly.ssyxsystem.user.mapper.LeaderMapper;
import com.ly.ssyxsystem.user.service.UserService;
import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import com.ly.ssyxsystem.vo.user.UserLoginVo;
import com.ly.ssyxsystem.user.mapper.UserDeliveryMapper;
import com.ly.ssyxsystem.user.mapper.UserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserDeliveryMapper userDeliveryMapper;

    @Autowired
    private LeaderMapper leaderMapper;

    @Override
    public User getUserByOpenId(String openId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getOpenId, openId);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public LeaderAddressVo getLeaderAddressByUserId(Long id) {

        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>()
//                        .eq(UserDelivery::getUserId, id)
                        .eq(UserDelivery::getIsDefault, 1)
        );

        if (userDelivery == null) {
            return null;
        }
        Leader leader = leaderMapper.selectById(userDelivery.getLeaderId());
        //封装数据到LeaderAddressVo
        LeaderAddressVo leaderAddressVo = new LeaderAddressVo();
        BeanUtils.copyProperties(leader, leaderAddressVo);
        leaderAddressVo.setUserId(id);
        leaderAddressVo.setLeaderId(leader.getId());
        leaderAddressVo.setLeaderName(leader.getName());
        leaderAddressVo.setLeaderPhone(leader.getPhone());
        leaderAddressVo.setWareId(userDelivery.getWareId());
        leaderAddressVo.setStorePath(leader.getStorePath());
        return leaderAddressVo;
    }

    @Override
    public UserLoginVo getUserLoginVo(Long id) {
        User user = baseMapper.selectById(id);
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUserId(id);
        userLoginVo.setNickName(user.getNickName());
        userLoginVo.setPhotoUrl(user.getPhotoUrl());
        userLoginVo.setIsNew(user.getIsNew());
        userLoginVo.setOpenId(user.getOpenId());

        UserDelivery userDelivery = userDeliveryMapper.selectOne(
                new LambdaQueryWrapper<UserDelivery>()
                        .eq(UserDelivery::getUserId, id)
                        .eq(UserDelivery::getIsDefault, 1)
        );
        if (userDelivery != null) {
            userLoginVo.setWareId(userDelivery.getWareId());
            userLoginVo.setLeaderId(userDelivery.getLeaderId());
        } else {
            userLoginVo.setWareId(1L);
            userLoginVo.setLeaderId(1L);
        }
        return userLoginVo;
    }
}
