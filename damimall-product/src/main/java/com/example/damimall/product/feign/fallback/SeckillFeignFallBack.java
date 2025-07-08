package com.example.damimall.product.feign.fallback;

import com.example.common.exception.BizCodeEnum;
import com.example.common.utils.R;
import com.example.damimall.product.feign.SeckillFeign;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Arrays;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

@Component
@Slf4j
public class SeckillFeignFallBack implements SeckillFeign {
    @Override
    public R seckillInfo(Long skuId) {
        log.info("熔断保护......seckillInfo");
        return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
    }
}
