package com.ly.ssyxsystem.config;


import com.ly.ssyxsystem.constant.MqConst;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkuConfig {

    @Bean
    public DirectExchange skuExchange() {
        return ExchangeBuilder.directExchange(MqConst.EXCHANGE_GOODS_DIRECT)
                .build();
    }

    @Bean
    public Queue skuUpperQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_UPPER)
                .build();
    }

    @Bean
    public Queue skuLowerQueue() {
        return QueueBuilder.durable(MqConst.QUEUE_GOODS_LOWER)
                .build();
    }

    @Bean
    public Binding skuUpperBinding() {
        return BindingBuilder.bind(skuUpperQueue())
               .to(skuExchange())
               .with(MqConst.ROUTING_GOODS_UPPER);
    }

    @Bean
    public Binding skuLowerBinding() {
        return BindingBuilder.bind(skuLowerQueue())
                .to(skuExchange())
                .with(MqConst.ROUTING_GOODS_LOWER);
    }

}
