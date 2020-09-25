package com.leyou.item.controller;


import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.sevice.BrandService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import java.util.List;

@Controller
@RequestMapping("/brand")
public class BrandController {


    @Autowired
    private BrandService brandService;

    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam("page") Integer page,
            @RequestParam("rows") Integer rows,
            @RequestParam("key") String key,
            @RequestParam("sortBy") String sortBy,
            @RequestParam("desc") Boolean desc
            ) {


        PageResult<Brand> brandPageResult = brandService.queryBrandByPage(key, page, rows, sortBy, desc);
        if (CollectionUtils.isEmpty(brandPageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brandPageResult);
    }

    /**
     * insert tb_brand、 insert tb_brand_category（关系表）
     * @param brand
     * @param cids 分类id
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> insertBrand(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.insertBrandAndCategory(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * update tb_brand、 update tb_brand_category（关系表）
     * @param brand
     * @param cids 分类id
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> update(Brand brand, @RequestParam("cids") List<Long> cids) {
        brandService.updateBrandAndCategory(brand, cids);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * delete brand
     * @param brand
     * @return
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteBrand(Brand brand) {
        brandService.deleteBrand(brand);
        return ResponseEntity.ok(null);
    }


    /**
     * query brand by cid
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandByCid(@PathVariable("cid") Long cid) {
        if (cid == null || cid.longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        List<Brand> brands = this.brandService.queryBrandByCid(cid);
        if (CollectionUtils.isEmpty(brands)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(brands);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        if (id == null || id.longValue() < 0) {
            return ResponseEntity.badRequest().build();
        }

        Brand brand = this.brandService.queryBrandById(id);

        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }
}
