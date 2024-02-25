package com.ly.ssyxsystem.user.controller;


import com.alibaba.fastjson.JSONObject;
import com.ly.ssyxsystem.auth.AuthContextHolder;
import com.ly.ssyxsystem.constant.RedisConst;
import com.ly.ssyxsystem.enums.UserType;
import com.ly.ssyxsystem.enums.user.User;
import com.ly.ssyxsystem.exception.SsyxException;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import com.ly.ssyxsystem.user.service.UserService;
import com.ly.ssyxsystem.user.util.ConstantPropertiesUtil;
import com.ly.ssyxsystem.user.util.HttpClientUtils;
import com.ly.ssyxsystem.utils.JwtHelper;
import com.ly.ssyxsystem.vo.user.LeaderAddressVo;
import com.ly.ssyxsystem.vo.user.UserLoginVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api111/user/weixin")
public class WeixinApiController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;


    // 用户微信授权登录
    @ApiOperation("微信登录获取openId(小程序)")
    @GetMapping("/wxLogin/{code}")
    @Transactional
    public Result loginWx(@PathVariable String code) {

        // 1. 得到微信返回code临时票据值
        // 2. 拿着 code + 小程序id + 小程序秘钥 请求微信接口服务
        String appId = ConstantPropertiesUtil.WX_OPEN_APP_ID;
        String appSecret = ConstantPropertiesUtil.WX_OPEN_APP_SECRET;
        String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appId
                + "&secret=" + appSecret
                + "&js_code=" + code
                + "&grant_type=authorization_code";
        String result = null;
        try {
            result = HttpClientUtils.get(url);
        } catch (Exception e) {
            throw new SsyxException(ResultCodeEnum.FETCH_ACCESSTOKEN_FAILD);
        }
        // 3. 请求微信接口服务，返回俩个值 session_key 和 openId
        // openId 是你微信唯一标识
        JSONObject jsonObject = JSONObject.parseObject(result);
        String sessionKey = jsonObject.getString("session_key");
        String openId = jsonObject.getString("openid");
        // 4. 添加用户信息到数据库中
        // 操作user表
        // 判断是否是第一次使用微信授权登录：如何判断?openId
        User user = userService.getUserByOpenId(openId);
        if (user == null) {
            user = new User();
            user.setOpenId(openId);
            user.setNickName(openId);
            user.setPhotoUrl("");
            user.setUserType(UserType.USER);
            user.setIsNew(0);
            userService.save(user);
        }

        // 5. 根据userId查询提货点和团长信息
        // 提货点   user_delivery 表
        // 团长     leader 表
        LeaderAddressVo leaderAddressVo =
                userService.getLeaderAddressByUserId(user.getId());

        // 6 使用JWT工具根据userId和userName生成token字符串
        // 上面save，会把自增的id填充到user中的
        String token = JwtHelper.createToken(user.getId(), user.getNickName());

        // 7 获取当前登录用户信息，放到redis里面，设置有效时间
        UserLoginVo userLoginVo = userService.getUserLoginVo(user.getId());
        redisTemplate.opsForValue().set(RedisConst.USER_LOGIN_KEY_PREFIX + user.getId(),
                userLoginVo,
                RedisConst.USERKEY_TIMEOUT,
                TimeUnit.DAYS);

        // 需要数据封装到map返回
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("token", token);
        map.put("leaderAddressVo", leaderAddressVo);

        return Result.ok(map);
    }


    @PostMapping("/auth/updateUser")
    @ApiOperation(value = "更新用户昵称与头像")
    public Result updateUser(@RequestBody User user) {
        //获取当前登录用户id
        User user1 = userService.getById(AuthContextHolder.getUserId());
        //把昵称更新为微信用户
        user1.setNickName(user.getNickName().replaceAll("[ue000-uefff]", "*"));
        user1.setPhotoUrl(user.getPhotoUrl());
        userService.updateById(user1);
        return Result.ok(null);
    }


}
