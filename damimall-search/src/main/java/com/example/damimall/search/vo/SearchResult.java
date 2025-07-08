package com.example.damimall.search.vo;

import com.example.common.to.search.SkuEsTo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class SearchResult {
    private List<SkuEsTo> products;

    // 分页信息
    private Integer pageNum;
    private Long total;
    private Integer totalPages;
    private List<Integer> pageNavs;

    private List<BrandVo> brands;
    private List<CatelogVo> catalogs;
    private List<AttrVo> attrs;
    private List<Long> attrIds;

    private List<NavVo> navs;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatelogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NavVo{
        private String navName;
        private String navValue;
        private String link;
    }
}
