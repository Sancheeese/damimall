package com.example.damimall.product.feign;

import com.example.common.to.search.SkuEsTo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("damimall-search")
public interface SearchFeignService {
    @RequestMapping("/elasticsearch/saveProduct")
    public boolean saveProduct(@RequestBody List<SkuEsTo> products);

    @RequestMapping("/elasticsearch/updateProduct")
    public boolean updateProduct(@RequestBody List<SkuEsTo> products);
}
