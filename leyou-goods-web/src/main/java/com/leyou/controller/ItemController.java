package com.leyou.controller;

import com.leyou.service.GoodsHtmlService;
import com.leyou.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class ItemController {


    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsHtmlService goodsHtmlService;

    @GetMapping("{spuId}.html")
    public String queryItemPage(@PathVariable("spuId") Long id, Model model) {

        Map<String, Object> modelMap = this.goodsService.loasData(id);
        model.addAllAttributes(modelMap);
        this.goodsHtmlService.asyncExcute(id);
        return "item";
    }


}
