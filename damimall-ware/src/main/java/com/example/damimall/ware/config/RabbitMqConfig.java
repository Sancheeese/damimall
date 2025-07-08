package com.example.damimall.ware.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {
    // json序列化
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    // 建立队列和交换器
    @Bean
    public Queue stockReleaseQueue(){
        return new Queue("stock.release.queue", true, false, false, null);
    }

    @Bean
    public Queue stockDelayQueue(){
        Map<String, Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange", "stock-event-exchange");
        params.put("x-dead-letter-routing-key", "stock.release");
        params.put("x-message-ttl", 60 * 1000);

        return new Queue("stock.delay.queue", true, false, false, params);
    }

    @Bean
    public Exchange stockEventExchange(){
        return new TopicExchange("stock-event-exchange", true, false, null);
    }

    @Bean
    public Binding stock2ReleaseBinding(){
        return new Binding("stock.release.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.release",
                null);
    }

    @Bean
    public Binding stock2DelayBinding(){
        return new Binding("stock.delay.queue",
                Binding.DestinationType.QUEUE,
                "stock-event-exchange",
                "stock.delay",
                null);
    }

//    @RabbitListener(queues = "stock.release.queue")
//    public void hello(){
//        System.out.println("hello");
//    }


}
