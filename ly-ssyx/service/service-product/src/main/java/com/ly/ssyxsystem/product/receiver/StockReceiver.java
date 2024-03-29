package com.ly.ssyxsystem.product.receiver;

import com.ly.ssyxsystem.constant.MqConst;
import com.ly.ssyxsystem.product.service.SkuInfoService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;

@Component
@Slf4j
public class StockReceiver {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 扣减库存成功，更新订单状态
     *
     * @param orderNo
     * @throws IOException
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_MINUS_STOCK, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_ORDER_DIRECT),
            key = {MqConst.ROUTING_MINUS_STOCK}
    ))
    public void minusStock(String orderNo,
                           Message message,
                           Channel channel) throws IOException {
        if (!StringUtils.isEmpty(orderNo)) {
            skuInfoService.minusStock(orderNo);
            log.info("更新了_{}_的库存", orderNo);
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }
}
