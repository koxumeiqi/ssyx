package com.ly.ssyxsystem.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.enums.user.User;
import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import com.ly.ssyxsystem.vo.user.UserLoginVo;

public interface UserService extends IService<User> {
    User getUserByOpenId(String openId);

    LeaderAddressVo getLeaderAddressByUserId(Long id);

    UserLoginVo getUserLoginVo(Long id);
}
