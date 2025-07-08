package com.example.damimall.product.vo.itemVo;

import com.example.damimall.product.vo.spuInfoVo.BaseAttr;
import lombok.Data;

import java.util.List;

@Data
public class SaleAttrItemVo {
    private Long attrId;
    private String attrName;
    private List<BaseAttrValueItemVo> attrValues;
}
