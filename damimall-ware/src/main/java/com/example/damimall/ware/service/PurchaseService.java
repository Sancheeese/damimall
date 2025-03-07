package com.example.damimall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.ware.entity.PurchaseEntity;
import com.example.damimall.ware.vo.MergeVo;
import com.example.damimall.ware.vo.PurchaseDoneVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:29:08
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void savePurchase(PurchaseEntity purchase);

    PageUtils listUnreceive(Map<String, Object> params);

    void mergeRequire(MergeVo mergeVo);

    void updatePurchaseById(PurchaseEntity purchase);

    void recevied(List<Long> ids);

    void donePurchase(PurchaseDoneVo purchaseDoneVo);
}

