package com.example.damimall.product.vo.webVo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category2LevelVo {
    private Long catalog1Id;

    private List<Category3LevelVo> catalog3List;

    private Long id;

    private String name;

    public Category2LevelVo(Long catalog1Id, Long id, String name) {
        this.catalog1Id = catalog1Id;
        this.id = id;
        this.name = name;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Category3LevelVo{
        private Long catalog2Id;

        private Long id;

        private String name;
    }
}
