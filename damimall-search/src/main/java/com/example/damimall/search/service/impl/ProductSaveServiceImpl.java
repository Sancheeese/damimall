package com.example.damimall.search.service.impl;

import com.example.common.constant.ProductConstant;
import com.example.common.to.search.SkuEsTo;
import com.example.damimall.search.config.ElasticsearchConfig;
import com.example.damimall.search.service.ProductSaveService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.index.IndexReader;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class ProductSaveServiceImpl implements ProductSaveService {
    @Autowired
    RestHighLevelClient elasticClient;

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean productUp(List<SkuEsTo> products) {
        try {
            BulkRequest bulkRequest = new BulkRequest();
            for (SkuEsTo product : products) {
                IndexRequest indexRequest = new IndexRequest(ProductConstant.PRODUCT_INDEX);
                indexRequest.id(product.getSkuId().toString());
                String productJson = objectMapper.writeValueAsString(product);
                indexRequest.source(productJson, XContentType.JSON);
                bulkRequest.add(indexRequest);
            }

            BulkResponse response = elasticClient.bulk(bulkRequest, ElasticsearchConfig.COMMON_OPTIONS);

            boolean hasFailures = response.hasFailures();
            String failMsg = response.buildFailureMessage();
            log.error("elastic保存失败：" + failMsg);
            return !hasFailures;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上架保存商品至elasticsearch出错");
        }

        return false;
    }
}
