package com.leyou.listen;

import com.leyou.service.GoodsHtmlService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {


    @Autowired
    private GoodsHtmlService goodsHtmlService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.web.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"item.insert", "item.update"}

    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }
        goodsHtmlService.createHtml(id);



    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.delete.web.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", type = ExchangeTypes.TOPIC),
            ignoreDeclarationExceptions = "true",key = {"item.delete"}

    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        goodsHtmlService.deleteHtml(id);



    }
}
