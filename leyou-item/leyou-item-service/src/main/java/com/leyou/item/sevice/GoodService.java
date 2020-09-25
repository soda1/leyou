package com.leyou.item.sevice;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class GoodService {

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SpuDetailMapper spuDetailMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodService.class);

    /**
     * 分页查询商品集
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuBoByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        PageHelper.startPage(page, rows);

        //查询出分页商品
        List<Spu> spus = this.spuMapper.selectByExample(example);

        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        List<SpuBo> spuBos = new ArrayList<>();

        spus.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);
            //查出分类
            List<String> names = this.categoryService.queryNamesByIds(spu.getCid1(), spu.getCid2(), spu.getCid3());
            spuBo.setCname(StringUtils.join(names, "/"));
            //查出品牌
            String brandName = this.brandMapper.selectByPrimaryKey(spu.getBrandId()).getName();
            spuBo.setBname(brandName);
            spuBos.add(spuBo);
        });
        return new PageResult<SpuBo>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 保存商品集及商品和库存
     * @param spuBo
     */
    @Transactional
    public void saveGood(SpuBo spuBo) {
        //设置必要字段
        spuBo.setId(null);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(new Date());
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        //插入spu
        this.spuMapper.insertSelective(spuBo);
        spuBo.getSpuDetail().setSpuId(spuBo.getId());
        //插入spuDetail
        this.spuDetailMapper.insertSelective(spuBo.getSpuDetail());

        //插入sku
        saveSkuAndStock(spuBo);
        sendMsg("insert", spuBo.getId());
    }



    /**
     * 通过spuId查询商品集下所有商品
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> select = this.skuMapper.select(sku);
        //把skus下的库存商品也查出来
        select.forEach(sku1 -> {
            Stock stock = this.skuMapper.queryStockBySkuId(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return select;
    }

    /**
     * 通过spuid查询商品集详情
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.spuDetailMapper.selectByPrimaryKey(spuId);

    }


    @Transactional
    /**
     * 编辑商品
     * @param spuBo
     */
    public void editGood(SpuBo spuBo) {
        //更新spu
        spuBo.setLastUpdateTime(new Date());
        this.spuMapper.updateByPrimaryKey(spuBo);

        //更新spuDetail
        this.spuDetailMapper.updateByPrimaryKey(spuBo.getSpuDetail());

        //更新sku
        //删除之前的sku
        deleteSkuAndStock(spuBo.getId());
        //插入新的sku
        saveSkuAndStock(spuBo);

        sendMsg("edit", spuBo.getId());

    }

    @Transactional
    /**
     * 删除商品
     * @param spuId
     */
    public void deleteGood(Long spuId) {
        this.spuMapper.deleteByPrimaryKey(spuId);
        this.spuDetailMapper.deleteByPrimaryKey(spuId);
        deleteSkuAndStock(spuId);
        sendMsg("delete", spuId);
    }

    public void GoodSaleOrNo(Boolean saleable) {

    }

    /**
     * 保存商品和库存
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {

            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(new Date());
            sku.setSpuId(spuBo.getId());
            this.skuMapper.insertSelective(sku);

            Stock stock = new Stock();
            stock.setSku_id(sku.getId());
            stock.setStock(sku.getStock());
            this.skuMapper.InsertStock(stock);

        });
    }


    /**
     * 通过spuId删除sku
     * @param spuId
     */
    private void deleteSkuAndStock(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);

        List<Sku> skuList = this.skuMapper.select(sku);
        //删除sku库存
        skuList.forEach(sku1 -> {
            this.skuMapper.deleteStock(sku1.getId());
        });
        //删除sku
        this.skuMapper.delete(sku);
    }

    public void editSpu(Spu spu) {
        spu.setLastUpdateTime(new Date());
        this.spuMapper.updateByPrimaryKeySelective(spu);
    }

    /**
     * 根据商品id查询商品
     * @param spuId
     * @return
     */
    public Spu querySpuById(Long spuId) {
        return this.spuMapper.selectByPrimaryKey(spuId);
    }


    public void sendMsg(String type, Long spuId) {
        try {
            amqpTemplate.convertAndSend("item." + type, spuId);
        } catch (Exception e) {
            LOGGER.error("消息发送错误:{}" + e, spuId);
        }
    }
}
