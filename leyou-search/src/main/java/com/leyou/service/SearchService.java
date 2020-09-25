package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.client.BrandClient;
import com.leyou.client.CategoryClient;
import com.leyou.client.GoodsClient;
import com.leyou.client.SpecificationClient;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.*;
import com.leyou.pojo.Goods;
import com.leyou.pojo.SearchRequest;
import com.leyou.pojo.SearchResult;
import com.leyou.repository.GoodsRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.lucene.queryparser.xml.builders.BooleanQueryBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SourceFilter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import sun.rmi.runtime.Log;

import java.io.IOException;
import java.util.*;

@Service
public class SearchService {

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;


    private static final ObjectMapper MAPPER = new ObjectMapper();


    public Goods buildGoods(Spu spu) throws IOException {

        Goods goods = new Goods();

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        List<String> categoryNames = this.categoryClient.queryNameByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());

        List<Long> prices = new ArrayList<>();

        List<Map<String, Object>> skuMapList = new ArrayList<>();

        //获取sku集合及价格集合
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> skuMap = new HashMap<>();
            skuMap.put("id", sku.getId());
            skuMap.put("title", sku.getTitle());
            skuMap.put("price", sku.getPrice());
            skuMap.put("image", StringUtils.isNotBlank(sku.getImages()) ? StringUtils.split(sku.getImages(), ",")[0] : "");
            skuMapList.add(skuMap);

        });

        //查询出所有搜索规格参数
        List<SpecParam> specParams = this.specificationClient.queryParams(null, spu.getCid3(), null, true);

        // 查询出spuDetail。获取规格参数值
        SpuDetail spuDetail = this.goodsClient.querySpuDetailById(spu.getId());

        //获取通用规格参数
        Map<Long, Object> genericSpecMap = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<Long, Object>>() {

        });

        if (genericSpecMap == null) {
            System.out.println("map is null");
        }

        //获取特殊规格参数
        Map<Long, List<Object>> specialSpecMap = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<Long, List<Object>>>() {
        });


        //定义map接收{规格参数名，规格参数值}
        Map<String, Object> paramMap = new HashMap<>();

        if (specParams == null) {
            System.out.println("specParams is null");
        }
        specParams.forEach(param -> {
            //判断是否时通用参数
            if (param.getGeneric()) {
//                System.out.println(genericSpecMap.get(7));

                String value = genericSpecMap.get(param.getId()).toString();
                if (param.getNumeric()) {
                    value = chooseSegment(value, param);

                }

                paramMap.put(param.getName(), value);
                System.out.println(value);
                System.out.println(param);

                //判断是否时数值类型

            } else {
                paramMap.put(param.getName(), specialSpecMap.get(param.getId()));
            }
        });


        //设置参数
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        goods.setAll(spu.getTitle() + brand.getName() + StringUtils.join(categoryNames, " "));
        goods.setPrice(prices);
        goods.setSkus(MAPPER.writeValueAsString(skus));
        goods.setSpecs(paramMap);
        return goods;


    }

    private String chooseSegment(String value, SpecParam param) {
        double val = NumberUtils.toDouble(value);
        String result = "其他";

        for (String s : param.getSegments().split(",")) {
            String[] segs = s.split("-");

            //获取数值范围
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;

            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }

            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + param.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + param.getUnit() + "以下";
                } else {
                    result = s + param.getUnit();
                }
                break;


            }

        }
        return result;
    }


    public SearchResult search(SearchRequest searchRequest) {

        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }

        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        //对key进行查询
        BoolQueryBuilder basicBoolQuery = buildBooleanQueryBuilder(searchRequest);
        queryBuilder.withQuery(basicBoolQuery);

        //通过sourceFilter设置返回字段

        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));


        //是否排序

        Boolean descending = searchRequest.getDescending();
        String sortBy = searchRequest.getSortBy();
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }


        //分页

        int page = searchRequest.getPage();
        int size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //获取查询结果

        Page<Goods> pageInfo = this.goodsRepository.search(queryBuilder.build());

        //对分类和品牌进行聚合查询,然后将查出来的分类及品牌放在可过滤区中

        String categoryName = "categories";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryName).field("cid3"));
        String brandsName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(brandsName).field("brandId"));


        AggregatedPage<Goods> aggregatedPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());


        //解析聚合
        List<Map<String, Object>> categories = getCategories(aggregatedPage.getAggregation(categoryName));

        List<Brand> brands = getBrands(aggregatedPage.getAggregation(brandsName));

        //对可选参数进行聚合,要求只有一个分类
        List<Map<String, Object>> paramsMapList = null;
        if (categories.size() == 1) {
            paramsMapList = getParamAggResult((Long) categories.get(0).get("id"), basicBoolQuery);
        }

        Long total = pageInfo.getTotalElements();

        int totalPage = (total.intValue() + size - 1) / size;


        return new SearchResult(total, totalPage, pageInfo.getContent(), brands, categories, paramsMapList);


    }

    /**
     * 从桶中获取categories
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategories(Aggregation aggregation) {

        LongTerms longTerms = (LongTerms) aggregation;
        List<LongTerms.Bucket> buckets = longTerms.getBuckets();
        List<Long> cids = new ArrayList<>();
        buckets.forEach(bucket -> {
            cids.add(bucket.getKeyAsNumber().longValue());
        });

        List<String> names = this.categoryClient.queryNameByIds(cids);

        List<Map<String, Object>> categoriesMap = new ArrayList<>();

        for (int i = 0; i < cids.size(); i++) {

            HashMap<String, Object> map = new HashMap<>();
            map.put("name", names.get(i));
            map.put("id", cids.get(i));
            categoriesMap.add(map);
        }

        return categoriesMap;


    }

    /**
     * 从桶中获取brands
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrands(Aggregation aggregation) {
        LongTerms longTerms = (LongTerms) aggregation;

        List<LongTerms.Bucket> buckets = longTerms.getBuckets();
        List<Long> brands = new ArrayList<>();
        buckets.forEach(bucket -> {
            brands.add(bucket.getKeyAsNumber().longValue());

        });

        List<Brand> brandList = new ArrayList<>();
        brands.forEach(brand -> {
            Brand brand1 = this.brandClient.queryBrandById(brand);
            brandList.add(brand1);
        });

        return brandList;
    }


    /**
     * 查询可过滤字段
     *
     * @param id
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long id, QueryBuilder basicQuery) {

        //定义一个查询器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        queryBuilder.withQuery(basicQuery);
        List<SpecParam> specParams = this.specificationClient.queryParams(null, id, null, true);

        //将参数名进行分桶
        specParams.forEach(specParam -> {
//            System.out.println("specs." + specParam.getName() + ".keyword");
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName()).field("specs." + specParam.getName() + ".keyword"));
        });

        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));


        //执行查询
        AggregatedPage<Goods> aggregatedPage = (AggregatedPage) this.goodsRepository.search(queryBuilder.build());
        Map<String, Aggregation> stringAggregationMap = aggregatedPage.getAggregations().asMap();

        //存放spec可搜索参数字段
        List<Map<String, Object>> specMap = new ArrayList<>();

        for (Map.Entry<String, Aggregation> aggregationEntry : stringAggregationMap.entrySet()) {

            Map<String, Object> map = new HashMap<>();

            map.put("k", aggregationEntry.getKey());


            Aggregation aggregation = aggregationEntry.getValue();
            //获取spec字符串值
            StringTerms terms = (StringTerms) aggregation;

            //将桶内的值放在list中
            List<String> options = new ArrayList<>();

            List<StringTerms.Bucket> buckets = terms.getBuckets();

            buckets.forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });

            map.put("options", options);

            specMap.add(map);
        }
        return specMap;

    }


    /**
     * 构造过滤器
     *
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder buildBooleanQueryBuilder(SearchRequest searchRequest) {
        BoolQueryBuilder booleanQueryBuilder = QueryBuilders.boolQuery();


        //基本查询
        booleanQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));

        if (CollectionUtils.isEmpty(searchRequest.getFilter())) {
            return booleanQueryBuilder;
        }

        Set<Map.Entry<String, String>> entries = searchRequest.getFilter().entrySet();
        for (Map.Entry<String, String> entry : entries) {
            String key = entry.getKey();

            if (StringUtils.equals(key, "品牌")) {
                key = "brandId";
            } else if (StringUtils.equals(key, "分类")) {
                key = "cid3";
            } else {
                key = "specs." + key + ".keyword";
            }

            booleanQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));


        }

        return booleanQueryBuilder;
    }


    public void createIndexes(Long id) throws IOException {


        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = buildGoods(spu);
        this.goodsRepository.save(goods);


    }


    public void deleteIndexes(Long id){
        this.goodsRepository.deleteById(id);
    }
}
