package com.ly.ssyxsystem.home.controller;

import com.ly.ssyxsystem.auth.AuthContextHolder;
import com.ly.ssyxsystem.home.service.ItemService;
import com.ly.ssyxsystem.result.Result;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "商品详情")
@RestController
@RequestMapping("api111/home")
public class ItemApiController {

    @Autowired
    private ItemService itemService;

    @GetMapping("item/{id}")
    public Result index(@PathVariable Long id) {
        Long userId = AuthContextHolder.getUserId();
        Map<String,Object> map = itemService.item(id,userId);
        return Result.ok(map);
    }
}
