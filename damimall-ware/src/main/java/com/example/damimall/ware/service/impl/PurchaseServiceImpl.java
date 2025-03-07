package com.example.damimall.ware.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.common.constant.WareConstant;
import com.example.damimall.ware.entity.PurchaseDetailEntity;
import com.example.damimall.ware.service.PurchaseDetailService;
import com.example.damimall.ware.service.WareSkuService;
import com.example.damimall.ware.vo.MergeVo;
import com.example.damimall.ware.vo.NotDonePurchase;
import com.example.damimall.ware.vo.PurchaseDoneVo;
import com.example.damimall.ware.vo.StockUpdateVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.ware.dao.PurchaseDao;
import com.example.damimall.ware.entity.PurchaseEntity;
import com.example.damimall.ware.service.PurchaseService;
import org.springframework.transaction.annotation.Transactional;


@Service("purchaseService")
public class PurchaseServiceImpl extends ServiceImpl<PurchaseDao, PurchaseEntity> implements PurchaseService {
    @Autowired
    PurchaseDetailService purchaseDetailService;

    @Autowired
    WareSkuService wareSkuService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void savePurchase(PurchaseEntity purchase) {
        purchase.setCreateTime(new Date());
        purchase.setUpdateTime(new Date());
        save(purchase);
    }

    @Override
    public PageUtils listUnreceive(Map<String, Object> params) {
        IPage<PurchaseEntity> page = this.page(
                new Query<PurchaseEntity>().getPage(params),
                new QueryWrapper<PurchaseEntity>()
                        .eq("status", WareConstant.AttrType.ORDER_BUILD.getCode()).or()
                        .eq("status", WareConstant.AttrType.ORDER_ALLOCATED.getCode())
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void mergeRequire(MergeVo mergeVo) {
        Long purchaseId = mergeVo.getPurchaseId();
        List<Long> itemIds = mergeVo.getItems();

        if (purchaseId == null){
            PurchaseEntity purchase = new PurchaseEntity();
            purchase.setPriority(0);
            purchase.setStatus(WareConstant.AttrType.ORDER_BUILD.getCode());
            purchase.setCreateTime(new Date());
            purchase.setUpdateTime(new Date());
            save(purchase);
            purchaseId = purchase.getId();
        }

        List<PurchaseDetailEntity> purchaseDetailList = new ArrayList<>();
        for (Long itemId : itemIds) {
            PurchaseDetailEntity purchaseDetail = new PurchaseDetailEntity();
            purchaseDetail.setId(itemId);
            purchaseDetail.setPurchaseId(purchaseId);
            purchaseDetail.setStatus(WareConstant.AttrType.REQUIRE_ALLOCATED.getCode());
            purchaseDetailList.add(purchaseDetail);
        }
        purchaseDetailService.updateBatchById(purchaseDetailList);
    }

    @Override
    public void updatePurchaseById(PurchaseEntity purchase) {
        purchase.setUpdateTime(new Date());
        updateById(purchase);
    }

    @Override
    @Transactional
    public void recevied(List<Long> ids) {
        List<PurchaseEntity> allPurchases = query().in("id", ids).list();
        List<PurchaseEntity> purchases = allPurchases.stream()
                .filter(purchase -> purchase.getStatus() <= WareConstant.AttrType.ORDER_ALLOCATED.getCode())
                .map(purchase -> {
                    purchase.setStatus(WareConstant.AttrType.ORDER_RECEIVE.getCode());
                    purchase.setUpdateTime(new Date());
                    return purchase;
                })
                .collect(Collectors.toList());

        updateBatchById(purchases);

        List<PurchaseDetailEntity> purchaseDetailEntities = new ArrayList<>();
        for (PurchaseEntity purchase : purchases) {
            Long purchaseId = purchase.getId();
            List<PurchaseDetailEntity> updateDetail = purchaseDetailService.query()
                    .eq("purchase_id", purchaseId)
                    .list()
                    .stream()
                    .map(p -> {
                        PurchaseDetailEntity up = new PurchaseDetailEntity();
                        up.setId(p.getId());
                        up.setStatus(WareConstant.AttrType.REQUIRE_RECEIVE.getCode());
                        return up;
                    })
                    .collect(Collectors.toList());
            purchaseDetailEntities.addAll(updateDetail);
        }
        purchaseDetailService.updateBatchById(purchaseDetailEntities);

    }

    @Override
    @Transactional
    public void donePurchase(PurchaseDoneVo purchaseDoneVo) {
        Long purchaseId = purchaseDoneVo.getId();
        List<NotDonePurchase> items = purchaseDoneVo.getItems();

        List<PurchaseDetailEntity> purchaseItems = purchaseDetailService.query()
                .eq("purchase_id", purchaseId).list();
        Integer status = WareConstant.AttrType.ORDER_FINISH.getCode();
        if (items != null && !items.isEmpty()) {
            status = WareConstant.AttrType.ORDER_ERROR.getCode();

            Set<Long> failItemIds = new HashSet<>();
            for (NotDonePurchase item : items) {
                failItemIds.add(item.getItemId());
            }
            Iterator<PurchaseDetailEntity> iterator = purchaseItems.iterator();
            while(iterator.hasNext()){
                PurchaseDetailEntity next = iterator.next();
                if (failItemIds.contains(next.getId()))
                    next.setStatus(WareConstant.AttrType.REQUIRE_ERROR.getCode());
                else next.setStatus(WareConstant.AttrType.REQUIRE_FINISH.getCode());
            }
        }
        // 更新采购单
        PurchaseEntity purchase = getById(purchaseId);
        purchase.setStatus(status);
        purchase.setUpdateTime(new Date());
        updateById(purchase);
        // 更新采购项
        purchaseDetailService.updateBatchById(purchaseItems);
        //增加库存
        List<StockUpdateVo> stockUpdateList = new ArrayList<>();
        for (PurchaseDetailEntity item : purchaseItems) {
            StockUpdateVo stockUpdateVo = new StockUpdateVo();
            stockUpdateVo.setSkuId(item.getSkuId());
            stockUpdateVo.setWareId(item.getWareId());
            stockUpdateVo.setAddStock(item.getSkuNum());
            stockUpdateList.add(stockUpdateVo);
        }
        wareSkuService.updateStock(stockUpdateList);

    }

}