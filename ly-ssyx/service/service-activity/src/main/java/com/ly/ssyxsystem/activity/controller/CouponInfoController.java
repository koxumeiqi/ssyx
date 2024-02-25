package com.ly.ssyxsystem.activity.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ly.ssyxsystem.activity.service.CouponInfoService;
import com.ly.ssyxsystem.model.activity.CouponInfo;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.vo.activity.CouponRuleVo;
import org.springframework.beans.ConfigurablePropertyAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin/activity/couponInfo")
public class CouponInfoController {

    @Autowired
    private CouponInfoService couponInfoService;

    // 优惠券的分页查询
    /*url: `${api_name}/${page}/${limit}`,
    method: 'get'*/
    @GetMapping("{page}/{limit}")
    public Result list(@PathVariable Long page, @PathVariable Long limit) {

        IPage<CouponInfo> pageModel = couponInfoService.selectPageCouponInfo(page, limit);
        return Result.ok(pageModel);
    }

    // 添加优惠券
    @PostMapping("save")
    public Result save(@RequestBody CouponInfo couponInfo) {
        couponInfoService.save(couponInfo);
        return Result.ok();
    }

    // 根据id查询优惠券
    @GetMapping("get/{id}")
    public Result get(@PathVariable Long id) {
        CouponInfo couponInfo = couponInfoService.getById(id);
        couponInfo.setCouponTypeString(couponInfo.getCouponType().getComment());
        couponInfo.setRangeTypeString(couponInfo.getRangeType().getComment());
        return Result.ok(couponInfo);
    }

    // 根据优惠券id查询规则数据
    /*url: `${api_name}/findCouponRuleList/${id}`,
    method: 'get'*/
    @GetMapping("findCouponRuleList/{id}")
    public Result findCouponRuleList(@PathVariable Long id) {
        Map<String, Object> map = couponInfoService.findCouponRuleList(id);
        return Result.ok(map);
    }


    // 添加优惠券的规则数据
    @PostMapping("saveCouponRule")
    public Result saveCouponRule(@RequestBody CouponRuleVo couponRuleVo) {
        couponInfoService.saveCouponRule(couponRuleVo);
        return Result.ok();
    }

    /*url: `${api_name}/update`,
    method: 'put',*/
    @PutMapping("update")
    public Result update(@RequestBody CouponInfo couponInfo) {
        couponInfoService.updateById(couponInfo);
        return Result.ok();
    }

    /*url: `${api_name}/remove/${id}`,
    method: 'delete'*/
    @DeleteMapping("remove/{id}")
    public Result remove(@PathVariable Long id) {
        couponInfoService.deleteById(id);
        return Result.ok();
    }


}
