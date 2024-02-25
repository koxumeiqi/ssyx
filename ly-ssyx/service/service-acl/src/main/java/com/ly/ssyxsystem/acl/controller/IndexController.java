package com.ly.ssyxsystem.acl.controller;


import com.ly.ssyxsystem.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Api(tags = "登录接口")
@RequestMapping("admin/acl/index")
public class IndexController {

    // login
    @PostMapping("/login")
    @ApiOperation("登录")
    public Result login() {
        // 返回 token 值
        String uuid = UUID.randomUUID().toString();
        Map<String, String> map = new HashMap<>();
//        map.put("token", uuid);
        map.put("token", "token-admin");
        return Result.ok(map);
    }

    // getInfo
    @GetMapping("/info")
    @ApiOperation("登录信息")
    public Result info() {
        Map<String, String> map = new HashMap<>();
        map.put("name", "admin");
        map.put("avatar", "https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif");
        return Result.ok(map);
    }

    // logout
    @PostMapping("logout")
    @ApiOperation("退出")
    public Result logout() {
        return Result.ok(null);
    }

}
