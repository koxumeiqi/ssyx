package com.ly.ssyxsystem.controller;


import com.ly.ssyxsystem.model.sys.Ware;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.service.WareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * 仓库表
 */
@RestController
@RequestMapping("/admin/sys/ware")
public class WareController {

    @Autowired
    private WareService wareService;

    //查询所有仓库列表
//    url: `${api_name}/findAllList`,
//    method: 'get'
    @ApiOperation("查询所有仓库列表")
    @RequestMapping("/findAllList")
    public Result findAllList() {
        List<Ware> list = wareService.list();
        return Result.ok(list);
    }

}
