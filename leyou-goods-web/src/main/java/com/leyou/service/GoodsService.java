package com.leyou.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.item.pojo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.rmi.runtime.Log;

import javax.sound.midi.Track;
import java.util.*;

@Service
public class GoodsService  {

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;


    public Map<String, Object> loasData(Long spuId) {

        Map<String, Object> map = new HashMap<>();
        //查询出spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        //查询出spudetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spuId);

        //查询出完整分类
        List<Long> cids = Arrays.asList(spu.getCid1(),spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNameByIds(cids);
        //封装分类信息
        List<Map<String, Object>> categories = new ArrayList<>();

        for (int i = 0; i < names.size(); i++) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", cids.get(i));
            map1.put("name", names.get(i));

            categories.add(map1);
        }



        //查询出skus

        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);

        //查询出规格参数组及规格参数名
        List<SpecGroup> specGroups = this.specificationClient.querySpecGroupByCid(spu.getCid3());

        //查询出特殊规格参数 因为spudetail虽然存储着特殊规格参数，但是是以id存储的，所以这里
        //查出特殊规格参数名及id生成一个map对应

        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), false, null);
        //键值对对应
       Map<Long, Object> paramsMap = new HashMap<>();

        specParams.forEach(specParam ->{

            paramsMap.put(specParam.getId(), specParam.getName());
        });
        //查询出品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());



        // 封装spu
        map.put("spu", spu);
        // 封装spuDetail
        map.put("spuDetail", spuDetail);
        // 封装sku集合
        map.put("skus", skus);
        // 分类
        map.put("categories", categories);
        // 品牌
        map.put("brand", brand);
        // 规格参数组
        map.put("groups", specGroups);
        // 查询特殊规格参数
        map.put("paramMap", paramsMap);
        return map;



    }

}
