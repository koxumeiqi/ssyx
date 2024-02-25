package com.ly.ssyxsystem.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class LoginMvcConfigurerAdapter extends WebMvcConfigurationSupport {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserLoginInterceptor(redisTemplate))
                .addPathPatterns("/api111/**")
                .excludePathPatterns("/api111/user/weixin/wxLogin");
        super.addInterceptors(registry);
    }
}
