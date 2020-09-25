package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tk.mybatis.mapper.common.Mapper;

import javax.persistence.Id;
import java.util.LinkedList;
import java.util.List;


public interface CategoryMapper extends Mapper<Category> {


    @Select("select id, name from tb_category where id in ( select  category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoriseByBrandId(Long bid);

    @Update("<script>" +
            "update tb_category" +
            "<set>" +
            "<if test = 'category1 != null and category1.name != null'> " +
            "name = #{category1.name}, " +
            "</if>" +
            "<if test = 'category1 != null and category1.isParent != null'> " +
            "is_parent = #{category1.isParent}, " +
            "</if>" +
            "</set>" +
            "where id = #{category1.id}" +
            "</script>")
    int updateCategoryById(@Param("category1") Category category1);


}
