import com.example.damimall.search.SearchApplication;
import com.example.damimall.search.config.ElasticsearchConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.PipelineAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Map;
import java.util.TreeSet;

@SpringBootTest(classes = SearchApplication.class)
public class SearchTest {
    @Autowired
    RestHighLevelClient client;

    @Test
    void test1() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

//        IndexRequest request = new IndexRequest("user");

//        request.id("1");
////        request.source("name", "zs", "age", 18);
//        User user = new User("zs", 18);
//        String userJson = objectMapper.writeValueAsString(user);
//        request.source(userJson, XContentType.JSON);
//
//        IndexResponse index = client.index(request, ElasticsearchConfig.COMMON_OPTIONS);
//        System.out.println(index);
//
//        DeleteRequest delRequest = new DeleteRequest("user", "1");
//
//        DeleteResponse delete = client.delete(delRequest, ElasticsearchConfig.COMMON_OPTIONS);
//        System.out.println(delete);

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("kibana_sample_data_flights");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("OriginWeather", "Sunny"));

        TermsAggregationBuilder destCountry = AggregationBuilders.terms("destTerm").field("DestCountry").size(10);
        destCountry.subAggregation(AggregationBuilders.avg("destPriceAvg").field("AvgTicketPrice"));
        searchSourceBuilder.aggregation(destCountry);
        AvgAggregationBuilder avgTicketPrice = AggregationBuilders.avg("priceAvg").field("AvgTicketPrice");
        searchSourceBuilder.aggregation(avgTicketPrice);

        searchRequest.source(searchSourceBuilder);

        SearchResponse response = client.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        System.out.println(response);

        Aggregations aggregations = response.getAggregations();
        Terms destTerm = aggregations.get("destTerm");
        for (Terms.Bucket bucket : destTerm.getBuckets()) {
            System.out.println(bucket.getKeyAsString() + "===" + bucket.getDocCount());
            Aggregations aggregations1 = bucket.getAggregations();
            Avg destPriceAvg = aggregations1.get("destPriceAvg");
            System.out.println("avg:" + destPriceAvg.getValue());
            System.out.println();
        }
    }

    @Data
    class User{
        private String name;
        private Integer age;

        public User(String name, Integer age) {
            this.name = name;
            this.age = age;
        }
    }
}
