package com.example.damimall.ware.dao;

import com.example.common.to.mq.LockDetailTo;
import com.example.damimall.ware.entity.WareOrderTaskDetailEntity;
import com.example.damimall.ware.entity.WareSkuEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.damimall.ware.vo.SkuWareVo;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 * 
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:29:08
 */
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    WareSkuEntity queryStockById(@Param("id") Long skuId);

    List<SkuWareVo> getAvailableWare(@Param("skuIds") List<Long> skuIds);

    Long lockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("count") Integer count);

    void unlockStock(@Param("details") List<WareOrderTaskDetailEntity> details);
}
