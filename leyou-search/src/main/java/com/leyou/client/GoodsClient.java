package com.leyou.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.api.GoodsApi;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import sun.rmi.runtime.Log;

import java.util.List;

@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {

}
