package com.ly.ssyxsystem;


import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    // 发送消息的方法
    public boolean sendMessage(String exchange,
                               String routingKey,
                               Object message) {
        try {
            rabbitTemplate.convertAndSend(exchange, routingKey,
                    message);
            return true;
        } catch (AmqpException e) {
            e.printStackTrace();
            return false;
        }
    }

}
