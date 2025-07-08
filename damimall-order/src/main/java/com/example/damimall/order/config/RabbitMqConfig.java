package com.example.damimall.order.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Slf4j
public class RabbitMqConfig {
    @Autowired
    RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void initRabbitTemplate(){
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             *
             * @param correlationData 当前消息的唯一关联
             * @param b 消息是否成功收到
             * @param s 失败原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean b, String s) {
                if (b) {
                    log.info("confirm...[" + correlationData +"]");
                }else{
                    log.info("fail to send message[" + correlationData + "]" + ":" + s);
                }
            }
        });

        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                log.info("return...code:" + returnedMessage.getReplyCode() + "message:" + returnedMessage.getMessage());
            }
        });
    }

    // json序列化
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 建立队列和交换器
    @Bean
    public Queue orderReleaseQueue(){
        return new Queue("order.release.order.queue", true, false, false, null);
    }

    @Bean
    public Queue orderDelayQueue(){
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "order-event-exchange");
        params.put("x-dead-letter-routing-key", "order.release");
        params.put("x-message-ttl", 30 * 1000);

        return new Queue("order.delay.queue", true, false, false, params);
    }

    @Bean
    public Exchange orderEventExchange(){
        return new TopicExchange("order-event-exchange", true, false, null);
    }

    @Bean
    public Binding order2ReleaseBinding(){
        return new Binding("order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release",
                null);
    }

    @Bean
    public Binding order2DelayBinding(){
        return new Binding("order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.delay",
                null);
    }

    @Bean
    public Binding order2StockBinding(){
        return new Binding("stock.release.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null);
    }

    @Bean
    public Queue orderSeckillQueue(){
        return new Queue("order.seckill.order.queue", true, false, false);
    }

    @Bean
    public Binding order2SeckillBinding(){
        return new Binding("order.seckill.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.seckill.order",
                null);
    }

//    @RabbitListener(queues = "stock.release.queue")
//    public void hello(){
//        System.out.println("hello");
//    }


}
