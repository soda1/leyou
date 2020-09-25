package com.leyou.item.mapper;


import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.BrandAndCategory;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BrandMapper  extends Mapper<Brand> {

    /**
     * 增加品牌和分类中间表数据
     * @param cid 分类表id
     * @param bid 品牌id
     * @return
     */
    @Insert("insert into tb_category_brand (category_id, brand_id) values(#{cid}, #{bid})")
    int insertBrandAndCategory(@Param("cid") Long cid, @Param("bid") Long bid);


    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    int deleteBrandAndCategoryByBid(Long bid);


    @Insert("<script>" +
            "insert into tb_category_brand ( category_id, brand_id)" +
            "values" +
            "<foreach collection = 'cids' item = 'item' separator =','>" +
            "(#{item}, #{bid})" +
            "</foreach>" +
            "</script>")
    int batchInsertBrandAndCategory(@Param("cids") List<Long> cids, @Param("bid") Long bid);

    @Select("select brand_id from tb_category_brand where category_id = #{cid}")
    List<BrandAndCategory> queryBrandAndCategoryByCid(Long cid);



}
