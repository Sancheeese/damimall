package com.example.damimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.to.mq.LockStockDetailTo;
import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.PageUtils;
import com.example.damimall.ware.entity.WareSkuEntity;
import com.example.damimall.ware.vo.StockUpdateVo;
import com.example.damimall.ware.vo.WareSkuLockVo;

import java.util.List;
import java.util.Map;

/**
 * 商品库存
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:29:08
 */
public interface WareSkuService extends IService<WareSkuEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils listByCondition(Map<String, Object> params);

    void updateStock(List<StockUpdateVo> stockUpdateList);

    List<SkuStockTo> queryStockByIds(List<Long> skuIds);

    void lockStock(WareSkuLockVo wareSkuLockVo);

    void unlockStock(LockStockDetailTo task);

    void unlockStock(String orderSn);
}

