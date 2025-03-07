package com.example.damimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.product.entity.SpuInfoEntity;
import com.example.damimall.product.vo.spuInfoVo.SpuVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:50
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuVo spuVo);

    PageUtils listByCondition(Map<String, Object> params);

    void spuUp(Long spuId);
}

