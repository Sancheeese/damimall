package com.example.damimall.order.listener;

import com.example.common.to.seckill.SeckillOrderTo;
import com.example.damimall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "order.seckill.order.queue")
public class OrderSeckillListener {
    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void listener(SeckillOrderTo order, Channel channel, Message message) throws IOException {
        System.out.println("收到秒杀单");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            orderService.dealSeckillOrder(order);
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            channel.basicNack(deliveryTag, false, true);
        }
    }

}
