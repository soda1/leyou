package com.leyou.item.sevice;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.BrandAndCategory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;


@Service
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<Brand> queryBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();

        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //分页数，配合mybatis实现，只需要这一行代码就可以返回想要的数据
        PageHelper.startPage(page, rows);

        if (StringUtils.isNotBlank(sortBy)) {
            example.setOrderByClause(sortBy + " " + (desc ?"desc": "asc"));
        }

        List<Brand> brands = brandMapper.selectByExample(example);
        //包装成pageInfo
        PageInfo<Brand> pageInfo = new PageInfo<Brand>(brands);

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());

    }


    @Transactional
    public void insertBrandAndCategory(Brand brand, List<Long> cids) {
        this.brandMapper.insertSelective(brand);
        cids.forEach(cid ->this.brandMapper.insertBrandAndCategory(cid, brand.getId()));
    }

    @Transactional
    public void updateBrandAndCategory(Brand brand, List<Long> cids) {

        this.brandMapper.updateByPrimaryKey(brand);
        this.brandMapper.deleteBrandAndCategoryByBid(brand.getId());
        this.brandMapper.batchInsertBrandAndCategory(cids, brand.getId());
    }

    @Transactional
    public void deleteBrand(Brand brand) {
        this.brandMapper.delete(brand);
        this.brandMapper.deleteBrandAndCategoryByBid(brand.getId());
    }

    public List<Brand> queryBrandByCid(Long cid) {

        List<Brand> brands = new ArrayList<>();
        List<BrandAndCategory> brandAndCategories = this.brandMapper.queryBrandAndCategoryByCid(cid);
        brandAndCategories.forEach(brandAndCategory -> {
            Brand brand = this.brandMapper.selectByPrimaryKey(brandAndCategory.getBrand_id());
            brands.add(brand);
        });
        return brands;
    }

    public Brand queryBrandById(Long id) {

        return this.brandMapper.selectByPrimaryKey(id);
    }
}
