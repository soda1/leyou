package com.leyou.service;


import com.leyou.common.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;

@Service
public class GoodsHtmlService {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private TemplateEngine templateEngine;


    private static final Logger LOGGER = LoggerFactory.getLogger(GoodsHtmlService.class);

    /**
     * 创建静态化页面
     * @param spuId
     */
    public void createHtml(Long spuId) {

        PrintWriter writer = null;
        try {

            //获取数据
            Map<String, Object> spuMap = this.goodsService.loasData(spuId);

            //创建thymeleaf上下文对象
            Context context = new Context();
            //将数据放入context中
            context.setVariables(spuMap);

            //创建输出流
            File file = new File("D:\\nginx\\html\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);
            //执行页面静态化方法
            templateEngine.process("item", context, writer);

        } catch (Exception e) {
            LOGGER.error("页面静态化出错:{}," + e, spuId);
        }finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 异步处理页面静态化
     * @param spuId
     */

    public void asyncExcute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
    }

    public void deleteHtml(Long id) {
        File file = new File("D:\\nginx\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
