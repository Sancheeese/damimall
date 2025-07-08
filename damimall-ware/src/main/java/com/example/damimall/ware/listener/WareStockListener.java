package com.example.damimall.ware.listener;

import com.example.common.to.mq.LockStockDetailTo;
import com.example.damimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = "stock.release.queue")
public class WareStockListener {
    @Autowired
    WareSkuService wareSkuService;

    @RabbitHandler
    public void doReleaseStock(LockStockDetailTo task, Channel channel, Message message) throws IOException {
        System.out.println("库存到期解锁...");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try{
            wareSkuService.unlockStock(task);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(deliveryTag, false, true);
        }
        channel.basicAck(deliveryTag, false);
    }

    @RabbitHandler
    public void doReleaseStock(String orderSn, Channel channel, Message message) throws IOException {
        System.out.println("订单取消库存解锁");
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try{
            wareSkuService.unlockStock(orderSn);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicNack(deliveryTag, false, true);
        }
        channel.basicAck(deliveryTag, false);
    }
}
