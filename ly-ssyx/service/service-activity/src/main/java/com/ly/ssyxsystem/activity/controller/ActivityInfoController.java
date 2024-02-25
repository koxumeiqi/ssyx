package com.ly.ssyxsystem.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ly.ssyxsystem.activity.service.ActivityInfoService;
import com.ly.ssyxsystem.client.product.ProductFeignClient;
import com.ly.ssyxsystem.model.activity.ActivityInfo;
import com.ly.ssyxsystem.model.product.SkuInfo;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.vo.activity.ActivityRuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/activity/activityInfo")
public class ActivityInfoController {

    @Autowired
    private ActivityInfoService activityInfoService;


    /*url: `${api_name}/${page}/${limit}`,
    method: 'get'*/
    @GetMapping("{page}/{limit}")
    public Result getPageList(@PathVariable int page, @PathVariable int limit) {
        Page<ActivityInfo> pageParam = new Page<>(page, limit);
        IPage<ActivityInfo> pageList = activityInfoService.getPageList(pageParam);
        return Result.ok(pageList);
    }

    /*url: `${api_name}/get/${id}`,
    method: 'get'*/
    @GetMapping("/get/{id}")
    public Result getById(@PathVariable String id) {
        ActivityInfo activityInfo = activityInfoService.getById(id);
        activityInfo.setActivityTypeString(activityInfo.getActivityType().getComment());
        return Result.ok(activityInfo);
    }

    /*url: `${api_name}/save`,
    method: 'post',
    data: role*/
    @PostMapping("/save")
    public Result save(@RequestBody ActivityInfo activityInfo) {
        activityInfoService.save(activityInfo);
        return Result.ok();
    }

    // 营销活动规则相关接口
    // 1. 根据活动 id 获取活动规则数据
    @GetMapping("findActivityRuleList/{id}")
    public Result findActivityRuleList(@PathVariable Long id) {
        Map<String, Object> activityRuleMap =
                activityInfoService.findActivityRuleList(id);
        return Result.ok(activityRuleMap);
    }

    // 2. 在活动里面添加规则数据
    @PostMapping("saveActivityRule")
    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
        activityInfoService.saveActivityRule(activityRuleVo);
        return Result.ok();
    }

    // 3. 根据关键字查询匹配 sku 信息
//    findSkuInfoByKeyword/${keyword}
    @GetMapping("findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable String keyword) {
        List<SkuInfo> list =
                activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(list);
    }

    
}
