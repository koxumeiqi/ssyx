package com.ly.ssyxsystem.payment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ly.ssyxsystem.RabbitService;
import com.ly.ssyxsystem.constant.MqConst;
import com.ly.ssyxsystem.enums.PaymentStatus;
import com.ly.ssyxsystem.enums.PaymentType;
import com.ly.ssyxsystem.exception.SsyxException;
import com.ly.ssyxsystem.model.order.OrderInfo;
import com.ly.ssyxsystem.model.order.PaymentInfo;
import com.ly.ssyxsystem.order.client.OrderFeignClient;
import com.ly.ssyxsystem.payment.mapper.PaymentInfoMapper;
import com.ly.ssyxsystem.payment.service.PaymentInfoService;
import com.ly.ssyxsystem.result.ResultCodeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;


@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentInfoService {


    @Autowired
    private OrderFeignClient orderFeignClient;

    @Autowired
    private RabbitService rabbitService;

    @Override
    public PaymentInfo getPaymentInfoByOrderNo(String orderNo) {
        return baseMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>()
                        .eq(PaymentInfo::getOrderNo, orderNo)
        );
    }

    @Override
    public PaymentInfo savePaymentInfo(String orderNo) {
        //远程调用调用，根据orderNo查询订单信息
        OrderInfo orderInfo = orderFeignClient.getOrderInfo(orderNo);
        if (orderInfo == null) {
            throw new SsyxException(ResultCodeEnum.DATA_ERROR);
        }
        //封装到PaymentInfo对象
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentType.WEIXIN);
        paymentInfo.setUserId(orderInfo.getUserId());
        paymentInfo.setOrderNo(orderInfo.getOrderNo());
        paymentInfo.setPaymentStatus(PaymentStatus.UNPAID);
        String subject = "userID:" + orderInfo.getUserId() + "下订单";
        paymentInfo.setSubject(subject);
        //paymentInfo.setTotalAmount(orderInfo.getTotalAmount());
        //TODO 为了测试
        paymentInfo.setTotalAmount(new BigDecimal("0.01"));

        //调用方法实现添加
        baseMapper.insert(paymentInfo);
        return paymentInfo;
    }

    /**
     * 1. 修改支付状态
     * 2. 减库存
     *
     * @param orderNo
     * @param resultMap
     */
    @Override
    public void paySuccess(String orderNo, Map<String, String> resultMap) {
        // 查询当前订单支付记录表状态是否已经支付
        PaymentInfo paymentInfo = baseMapper.selectOne(
                new LambdaQueryWrapper<PaymentInfo>()
                        .eq(PaymentInfo::getOrderNo, orderNo)
        );
        if (paymentInfo.getPaymentStatus() != PaymentStatus.UNPAID) {
            return;
        }

        //2 如果支付记录表支付状态没有支付，更新
        paymentInfo.setPaymentStatus(PaymentStatus.PAID);
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);

        //3 整合RabbitMQ实现 修改订单记录已经支付，库存扣减
        rabbitService.sendMessage(MqConst.EXCHANGE_PAY_DIRECT,
                MqConst.ROUTING_PAY_SUCCESS, orderNo);
    }
}
