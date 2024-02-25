package com.ly.ssyxsystem.payment.controller;

import com.ly.ssyxsystem.payment.service.PaymentInfoService;
import com.ly.ssyxsystem.payment.service.WeixinService;
import com.ly.ssyxsystem.result.Result;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/api111/payment/weixin")
public class WeixinController {

    @Autowired
    private WeixinService weixinService;

    @Autowired
    private PaymentInfoService paymentInfoService;

    @GetMapping("/createJsapi/{orderNo}")
    public Result createJsapi(@PathVariable("orderNo") String orderNo) {
        Map<String, String> res = weixinService.createJsapi(orderNo);
        return Result.ok(res);
    }

    //查询订单支付状态
    @GetMapping("/queryPayStatus/{orderNo}")
    public Result queryPayStatus(@PathVariable("orderNo") String orderNo) {
        //1 调用微信支付系统接口查询订单支付状态
        Map<String, String> resultMap = weixinService.queryPayStatus(orderNo);

        //2 微信支付系统返回值为null，支付失败
        if (resultMap == null) {
            return Result.build(null, ResultCodeEnum.PAYMENT_FAIL);
        }

        //3 如果微信支付系统返回值，判断支付成功
//        if ("SUCCESS".equals(resultMap.get("trade_state"))) {
        // 模拟支付
        String out_trade_no = resultMap.get("out_trade_no");
        paymentInfoService.paySuccess(orderNo, resultMap);
        return Result.ok(null);
//        }

        //4 支付中，等待
//        return Result.build(null, ResultCodeEnum.PAYMENT_WAITING);
    }

}
