package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.sevice.GoodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@RequestMapping("/spu")
public class GoodsController {

    @Autowired
    private GoodService goodService;

    /**
     * 分页查询商品
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBo>>  querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "saleable", required = false)Boolean saleable,
            @RequestParam(value = "page", defaultValue = "1")Integer page,
            @RequestParam(value = "rows", defaultValue = "5")Integer rows
            ) {
        PageResult<SpuBo> pageResult = goodService.querySpuBoByPage(key, saleable, page, rows);
        if (CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 通过spuId查出spu
     * @param spuId
     * @return
     */

    @GetMapping("/spu/{spuId}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("spuId") Long spuId) {
        Spu spu = this.goodService.querySpuById(spuId);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);
    }

    /**
     * 通过spuId修改spu
     * @param spu
     * @param spuId
     * @return
     */
    @PutMapping("/spu/{spuId}")
    public ResponseEntity<Void> editGoodSale(@RequestBody Spu spu, @PathVariable("spuId") Long spuId) {

        spu.setSaleable(!spu.getSaleable());
        spu.setId(spuId);
        this.goodService.editSpu(spu);
        return ResponseEntity.ok(null);
    }

    /**
     * 获取商品集详细参数
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        if (spuId == null || spuId.longValue() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        SpuDetail spuDetail = this.goodService.querySpuDetailBySpuId(spuId);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);

    }

    /**
     * 获取spu下的所有sku
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam("id")Long spuId) {
        if (spuId == null || spuId.longValue() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        List<Sku> skus = this.goodService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skus)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skus);
    }


    /**
     * 保存商品
     * @param spuBo
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> insertGood(@RequestBody SpuBo spuBo) {
        this.goodService.saveGood(spuBo);
        return ResponseEntity.ok(null);
    }


    @PutMapping("/goods")
    public ResponseEntity<Void> editGood(@RequestBody SpuBo spuBo) {
        this.goodService.editGood(spuBo);
        return ResponseEntity.ok(null);
    }



}
