package com.ly.ssyxsystem.payment.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ly.ssyxsystem.model.order.PaymentInfo;

import java.util.Map;

public interface PaymentInfoService extends IService<PaymentInfo> {
    PaymentInfo getPaymentInfoByOrderNo(String orderNo);

    PaymentInfo savePaymentInfo(String orderNo);

    void paySuccess(String outTradeNo, Map<String, String> resultMap);

}
