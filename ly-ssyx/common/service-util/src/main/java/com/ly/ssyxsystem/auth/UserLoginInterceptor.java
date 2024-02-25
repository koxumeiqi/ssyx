package com.ly.ssyxsystem.auth;

import com.ly.ssyxsystem.constant.RedisConst;
import com.ly.ssyxsystem.utils.JwtHelper;
import com.ly.ssyxsystem.vo.user.UserLoginVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;

public class UserLoginInterceptor implements HandlerInterceptor {

    private RedisTemplate redisTemplate;

    public UserLoginInterceptor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getRequestURI().startsWith("/api111/user/weixin/wxLogin")) {
            return true;
        }
        return this.getUserLoginVo(request, response);
    }

    private boolean getUserLoginVo(HttpServletRequest request,
                                   HttpServletResponse response) {

        // 从请求头中获取到token
        String token = request.getHeader("token");
        // 判断token不为空
        if (!StringUtils.isEmpty(token)) {
            // 从token中获取userId
            Long userId = JwtHelper.getUserId(token);
            // 根据UserId到redis中获取对应的用户信息
            UserLoginVo userLoginVo = (UserLoginVo) redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_KEY_PREFIX
                    + userId);
            // 获取数据放入到ThreadLocal中
            if (userLoginVo != null) {
                AuthContextHolder.setUserLoginVo(userLoginVo);
                AuthContextHolder.setUserId(userId);
                AuthContextHolder.setWareId(userLoginVo.getWareId());
            }
            return true;
        }
        return true;

    }
}
