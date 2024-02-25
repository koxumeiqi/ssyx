package com.ly.ssyxsystem.search.recieve;

import com.ly.ssyxsystem.constant.MqConst;
import com.ly.ssyxsystem.search.service.SkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class SkuReceive {

    @Autowired
    private SkuService skuService;


    // 上架监听
    @RabbitListener(
            queues = MqConst.QUEUE_GOODS_UPPER
    )
    public void upperSku(Long skuId,
                         Message message,
                         Channel channel) {
        Optional<Long> skuOp = Optional.of(skuId);
        try {
            skuOp.ifPresent(
                    skuService::upperSku
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                    false);
        } catch (IOException e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                        false, false);
            } catch (IOException ex) {
                e.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    // 下架监听
    @RabbitListener(
            queues = MqConst.QUEUE_GOODS_LOWER
    )
    public void lowerSku(Long skuId,
                         Message message,
                         Channel channel) {
        Optional<Long> skuOp = Optional.of(skuId);
        try {
            skuOp.ifPresent(
                    skuService::lowerSku
            );
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),
                    false);
        } catch (IOException e) {
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(),
                        false, false);
            } catch (IOException ex) {
            }
        }
    }
}
