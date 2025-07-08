package com.example.damimall.search.service.impl;

import com.example.common.constant.ProductConstant;
import com.example.common.to.search.SkuEsTo;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.ParamUtils;
import com.example.damimall.search.config.ElasticsearchConfig;
import com.example.damimall.search.service.MallSearchService;
import com.example.damimall.search.vo.SearchParam;
import com.example.damimall.search.vo.SearchResult;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    RestHighLevelClient esClient;

    @Override
    public SearchResult search(SearchParam param) {
        SearchResult searchResult = null;
        SearchSourceBuilder sourceBuilder = buildSearchSource(param);
        SearchRequest searchRequest = new SearchRequest(new String[]{ProductConstant.PRODUCT_INDEX}, sourceBuilder);

        try {
            SearchResponse search = esClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
            searchResult = parseESResponse(search, param);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("查询elasticsearch出错");
        }
        return searchResult;
    }

//    构建dsl
    private SearchSourceBuilder buildSearchSource(SearchParam param){
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        // 搜索部分
        // 搜索关键词
        if (!ParamUtils.isNullOrEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 品牌
        if (param.getBrandId() != null && param.getBrandId().size() > 0){
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 分类
        if (param.getCatalog3Id() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("catelogId", param.getCatalog3Id()));
        }
        // 是否有库存
        if (param.getHasStock() != null){
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock().equals(1)));
        }
        // 价格区间
        if (!ParamUtils.isNullOrEmpty(param.getSkuPrice())){
            String priceStr = param.getSkuPrice();
            String[] priceSub = priceStr.split("_");
            RangeQueryBuilder skuPriceRange = QueryBuilders.rangeQuery("skuPrice");
            if (priceSub.length == 2) skuPriceRange.gte(Double.parseDouble(priceSub[0])).lte(Double.parseDouble(priceSub[1]));
            else if (priceStr.startsWith("_")) skuPriceRange.lte(Double.parseDouble(priceSub[0]));
            else skuPriceRange.gte(Double.parseDouble(priceSub[0]));
            boolQueryBuilder.filter(skuPriceRange);
        }
        // 属性
        if (param.getAttrs() != null && param.getAttrs().size() > 0){
            for (String attrStr : param.getAttrs()) {
                BoolQueryBuilder attrQuery = QueryBuilders.boolQuery();
                String[] attrSubStr = attrStr.split("_");
                Long attrId = Long.parseLong(attrSubStr[0]);
                List<String> values = new ArrayList<>();
                for (String value : attrSubStr[1].split(":")) {
                    attrQuery.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                    values.add(value);
                }
                attrQuery.must(QueryBuilders.termsQuery("attrs.attrValue", values));
                NestedQueryBuilder nestAttrQuery = QueryBuilders.nestedQuery("attrs", attrQuery, ScoreMode.None);
                boolQueryBuilder.filter(nestAttrQuery);
            }
        }

        //排序、分页
        if (!ParamUtils.isNullOrEmpty(param.getSort())){
            String[] sortSubStr = param.getSort().split("_");
            if (sortSubStr[1].equals("desc")) sourceBuilder.sort(sortSubStr[0], SortOrder.DESC);
            else if (sortSubStr[0].equals("asc")) sourceBuilder.sort(sortSubStr[0], SortOrder.ASC);
        }
        if (param.getPageNum() != null){
            Integer from = (param.getPageNum() - 1) * ProductConstant.PAGE_SIZE;
            sourceBuilder.from(from);
            sourceBuilder.size(ProductConstant.PAGE_SIZE);
        }

        // 聚合部分
        // 品牌
        TermsAggregationBuilder brandIdAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(30);
        brandIdAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandIdAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg")).size(1);
        sourceBuilder.aggregation(brandIdAgg);
        // 分类
        TermsAggregationBuilder catIdAgg = AggregationBuilders.terms("cat_agg").field("catelogId").size(30);
        catIdAgg.subAggregation(AggregationBuilders.terms("cat_name_agg").field("catelogName.keyword").size(1));
        sourceBuilder.aggregation(catIdAgg);
        // 属性
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId").size(50);
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        NestedAggregationBuilder attrsAggregationBuilder = AggregationBuilders.nested("attr_agg", "attrs")
                .subAggregation(attrIdAgg);

        sourceBuilder.aggregation(attrsAggregationBuilder);
        sourceBuilder.query(boolQueryBuilder);
        System.out.println(sourceBuilder);
        return sourceBuilder;
    }

    private SearchResult parseESResponse(SearchResponse response, SearchParam param){
        SearchResult searchResult = new SearchResult();
        SearchHits hits = response.getHits();
        List<SkuEsTo> skuEsTos = new ArrayList<>();
        Map<Long, String> attrId2Name = new HashMap<>();
        if (hits != null && hits.getHits().length > 0) {
            for (SearchHit hit : hits) {
                String skuEsToStr = hit.getSourceAsString();
                SkuEsTo skuEsTo = ObjectMapperUtils.readValue(skuEsToStr, new TypeReference<SkuEsTo>() {
                });
                skuEsTos.add(skuEsTo);
            }
        }
        searchResult.setProducts(skuEsTos);

        // 分页信息
        long total = hits.getTotalHits().value;
        int totalPages = (int) (total % ProductConstant.PAGE_SIZE == 0 ? total / ProductConstant.PAGE_SIZE : total / ProductConstant.PAGE_SIZE + 1);
        searchResult.setPageNum(param.getPageNum() == null ? 1 : param.getPageNum());
        searchResult.setTotal(total);
        searchResult.setTotalPages(totalPages);
        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPages; i++){
            pageNavs.add(i);
        }
        searchResult.setPageNavs(pageNavs);

        // 聚合信息
        Aggregations aggs = response.getAggregations();
        // 品牌
        ParsedLongTerms brandAgg = aggs.get("brand_agg");
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        Map<Long, String> brandId2Name = new HashMap<>();
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            Long brandId = Long.parseLong(bucket.getKeyAsString());
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo(brandId, brandName, brandImg);
            brandVos.add(brandVo);
            brandId2Name.put(brandId, brandName);
        }
        searchResult.setBrands(brandVos);

        // 类别
        ParsedLongTerms catAgg = aggs.get("cat_agg");
        List<SearchResult.CatelogVo> catelogVos = new ArrayList<>();
        for (Terms.Bucket bucket : catAgg.getBuckets()) {
            Long catId = Long.parseLong(bucket.getKeyAsString());
            ParsedStringTerms catNameAgg = bucket.getAggregations().get("cat_name_agg");
            String catName = catNameAgg.getBuckets().get(0).getKeyAsString();
            SearchResult.CatelogVo catelogVo = new SearchResult.CatelogVo(catId, catName);
            catelogVos.add(catelogVo);
        }
        searchResult.setCatalogs(catelogVos);

        // 属性
        ParsedNested attrAgg = aggs.get("attr_agg");
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
        List<SearchResult.AttrVo> attrVos = new ArrayList<>();
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            Long attrId = Long.parseLong(bucket.getKeyAsString());
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            Set<String> valueSet = new HashSet<>();
            for (Terms.Bucket b : attrValueAgg.getBuckets()) {
                String value = b.getKeyAsString();
                valueSet.add(value);
            }
            List<String> attrValues = new ArrayList<>(valueSet);
            SearchResult.AttrVo attrVo = new SearchResult.AttrVo(attrId, attrName, attrValues);
            attrVos.add(attrVo);
            attrId2Name.put(attrId, attrName);
        }
        searchResult.setAttrs(attrVos);

        // 面包屑导航
        setNav(searchResult, param, attrId2Name, brandId2Name);

        return searchResult;
    }

    // 面包屑导航
    public void setNav(SearchResult searchResult, SearchParam param, Map<Long, String> attrId2Name, Map<Long, String> brandId2Name){
        List<SearchResult.NavVo> navVos = new ArrayList<>();
        // 属性
        List<Long> attrIds = new ArrayList<>();// 记录出现过的属性id，以便面包屑导航使用
        if (param.getAttrs() != null && !param.getAttrs().isEmpty()){
            List<String> attrs = param.getAttrs();
            for (String attr : attrs) {
                String[] attrSubStr = attr.split("_");
                Long attrId = Long.parseLong(attrSubStr[0]);
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                // map没有就跳过
                if (!attrId2Name.containsKey(attrId)) continue;
                navVo.setNavName(attrId2Name.get(attrId));
                navVo.setNavValue(attrSubStr[1]);
                attrIds.add(attrId);
                // 创建link
                String queryString = null;
                try {
                    queryString = URLDecoder.decode(param.get_queryString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String toReplace = "attrs=" + attr;
                int i = queryString.indexOf(toReplace);
                toReplace = (queryString.charAt(i - 1) == '&' ? "&" : "") + toReplace;
                String newURL = queryString.replace(toReplace, "");
                navVo.setLink("http://search.damimall.com/list.html?" + newURL);

                navVos.add(navVo);
            }
        }

        // 品牌
        if (param.getBrandId() != null && !param.getBrandId().isEmpty()){
            List<Long> brandIds = param.getBrandId();
            for (Long brandId : brandIds) {
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                navVo.setNavName("品牌");
                if (!brandId2Name.containsKey(brandId)) continue;
                navVo.setNavValue(brandId2Name.get(brandId));
                // 创建link
                String queryString = null;
                try {
                    queryString = URLDecoder.decode(param.get_queryString(), "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String toReplace = "brandId=" + brandId;
                int i = queryString.indexOf(toReplace);
                toReplace = (queryString.charAt(i - 1) == '&' ? "&" : "") + toReplace;
                String newURL = queryString.replace(toReplace, "");
                navVo.setLink("http://search.damimall.com/list.html?" + newURL);
                navVos.add(navVo);
            }
        }

        searchResult.setNavs(navVos);
        searchResult.setAttrIds(attrIds);
    }

}
