package com.leyou.listen;


import com.leyou.repository.GoodsRepository;
import com.leyou.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SearchServiceListener {

    @Autowired
    private SearchService searchService;



    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.create.search.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }

        try {
            searchService.createIndexes(id);
        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "leyou.delete.search.queue", durable = "true"),
            exchange = @Exchange(value = "leyou.item.exchange", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
            key = { "item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }

        try {
            searchService.deleteIndexes(id);
        } catch (Exception e) {

            e.printStackTrace();
        }
    }

}
