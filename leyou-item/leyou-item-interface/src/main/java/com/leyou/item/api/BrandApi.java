package com.leyou.item.api;

import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/brand")
public interface BrandApi {

    @RequestMapping("{id}")
    Brand queryBrandById(@PathVariable("id") Long id);

}