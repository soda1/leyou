package com.leyou.item.mapper;

import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Stock;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

public interface SkuMapper extends Mapper<Sku> {

    @Insert("insert into tb_stock (sku_id,seckill_stock, seckill_total, stock) values(#{sku_id}, #{seckillStock}," +
            "#{seckillTotal}, #{stock})")
    int InsertStock(Stock stock);

    @Select("select stock from tb_stock where sku_id = #{skuId}")
    Stock queryStockBySkuId(Long skuId);

    @Delete("delete from tb_stock where sku_id = #{skuId}")
    int deleteStock(Long skuId);
}
