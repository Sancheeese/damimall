package com.example.damimall.product.vo.itemVo;

import lombok.Data;
import java.util.List;

@Data
public class AttrGroupItemVo {
    private Long groupId;
    private String groupName;
    private List<BaseAttrItemVo> attrs;
}
