package com.ly.ssyxsystem.home.controller;


import com.ly.ssyxsystem.auth.AuthContextHolder;
import com.ly.ssyxsystem.home.service.HomeService;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.vo.search.SkuEsQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

@RestController
@RequestMapping("/api111/home")
@Api(tags = "首页接口")
public class HomeApiController {

    @Autowired
    private HomeService homeService;


    @ApiOperation("首页数据显示接口")
    @GetMapping("/index")
    public Result index() {
        Long userId = AuthContextHolder.getUserId();
        Map<String, Object> map = homeService.homeData(userId);
        return Result.ok(map);
    }

/*    // 查询分类商品信息
    @GetMapping("/{page}/{limit}")
    public Result listSku(@PathVariable Long page,
                          @PathVariable Long limit,
                          SkuEsQueryVo skuEsQueryVo) {

        return Result.ok();
    }*/


}
