package com.example.damimall.ware.service.impl;

import com.example.common.to.mq.LockDetailTo;
import com.example.common.to.mq.LockStockDetailTo;
import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.ware.entity.WareOrderTaskDetailEntity;
import com.example.damimall.ware.entity.WareOrderTaskEntity;
import com.example.damimall.ware.exception.NoStockException;
import com.example.damimall.ware.feign.OrderFeign;
import com.example.damimall.ware.feign.ProductFeignService;
import com.example.damimall.ware.service.WareOrderTaskDetailService;
import com.example.damimall.ware.service.WareOrderTaskService;
import com.example.damimall.ware.vo.OrderItemVo;
import com.example.damimall.ware.vo.SkuWareVo;
import com.example.damimall.ware.vo.StockUpdateVo;
import com.example.damimall.ware.vo.WareSkuLockVo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.ware.dao.WareSkuDao;
import com.example.damimall.ware.entity.WareSkuEntity;
import com.example.damimall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareSkuDao wareSkuDao;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    WareOrderTaskService wareOrderTaskService;

    @Autowired
    WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    OrderFeign orderFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                new QueryWrapper<WareSkuEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listByCondition(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> queryWrapper = new QueryWrapper<>();

        if (!ParamUtils.isNullOrEmpty(params, "skuId")){
            String skuId = (String) params.get("skuId");
            queryWrapper.eq("sku_id", skuId);
        }

        if (!ParamUtils.isNullOrEmpty(params, "wareId")){
            String wareId = (String) params.get("wareId");
            queryWrapper.eq("ware_id", wareId);
        }

        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateStock(List<StockUpdateVo> stockUpdateList) {
        for (StockUpdateVo stockUpdateVo : stockUpdateList) {
            WareSkuEntity exist = query().eq("sku_id", stockUpdateVo.getSkuId())
                    .eq("ware_id", stockUpdateVo.getWareId())
                    .one();
            if (exist != null){
                exist.setStock(exist.getStock() + stockUpdateVo.getAddStock());
                updateById(exist);
            }else{
                WareSkuEntity wareSku = new WareSkuEntity();
                wareSku.setSkuId(stockUpdateVo.getSkuId());
                wareSku.setWareId(stockUpdateVo.getWareId());
                wareSku.setStock(stockUpdateVo.getAddStock());
                wareSku.setStockLocked(0);
                R r = productFeignService.getSkuById(stockUpdateVo.getSkuId());
                Map<String, Object> map = (Map<String, Object>) r.get("skuInfo");
                wareSku.setSkuName(map.get("skuName").toString());
                save(wareSku);
            }
        }
    }

    @Override
    public List<SkuStockTo> queryStockByIds(List<Long> skuIds) {
        List<SkuStockTo> skuStockTos = skuIds.stream().map(skuId -> {
            SkuStockTo skuStockTo = new SkuStockTo();
            WareSkuEntity wareSkuEntity = wareSkuDao.queryStockById(skuId);
            if (wareSkuEntity != null) BeanUtils.copyProperties(wareSkuEntity, skuStockTo);
            else {
                skuStockTo.setSkuId(skuId);
                skuStockTo.setStock(0);
            }
            return skuStockTo;
        }).collect(Collectors.toList());

        return skuStockTos;
    }

    // 锁定库存
    @Override
    @Transactional
    public void lockStock(WareSkuLockVo wareSkuLockVo) {
        // 保存锁库存单
        WareOrderTaskEntity task = new WareOrderTaskEntity();
        task.setOrderSn(wareSkuLockVo.getOrderSn());
        wareOrderTaskService.save(task);

        List<Long> skuIds = wareSkuLockVo.getLocks().stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
        Map<Long, List<Long>> sku2ware = getAvailableWare(skuIds);

        // 对每个item锁库存
        List<WareOrderTaskDetailEntity> taskDetailEntities = new ArrayList<>();
        for (OrderItemVo item : wareSkuLockVo.getLocks()) {
            Long skuId = item.getSkuId();
            List<Long> wareIds = sku2ware.get(skuId);
            // 有一个没库存就失败
            if (wareIds == null || wareIds.isEmpty()) throw new NoStockException(skuId);

            for (Long wareId : wareIds) {
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, item.getCount());
                if (count < 1) throw new NoStockException(skuId);
                WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
                detailEntity.setSkuId(skuId);
                detailEntity.setWareId(wareId);
                detailEntity.setSkuNum(item.getCount());
                detailEntity.setTaskId(task.getId());
                detailEntity.setLockStatus(1);

                taskDetailEntities.add(detailEntity);
                break;
            }
        }

        // 到这里就是锁库存成功了，将锁库存详情发到队列里
        // 保存数据库
        wareOrderTaskDetailService.saveBatch(taskDetailEntities);
        List<LockDetailTo> details = taskDetailEntities.stream().map(e -> {
            LockDetailTo lockDetailTo = new LockDetailTo();
            BeanUtils.copyProperties(e, lockDetailTo);
            return lockDetailTo;
        }).collect(Collectors.toList());
        // 发队列
        LockStockDetailTo to = new LockStockDetailTo();
        to.setOrderSn(task.getOrderSn());
        to.setDetails(details);
        rabbitTemplate.convertAndSend("stock-event-exchange", "stock.delay", to);
    }

    private Map<Long, List<Long>> getAvailableWare(List<Long> skuIds) {
        List<SkuWareVo> sku2ware = wareSkuDao.getAvailableWare(skuIds);
        Map<Long, List<Long>> ret = new HashMap<>();
        for (SkuWareVo skuWareVo : sku2ware) {
            Long skuId = skuWareVo.getSkuId();
            ret.put(skuId, skuWareVo.getWareId());
        }

        return ret;
    }

    @Override
    @Transactional
    public void unlockStock(LockStockDetailTo task) {
        String orderSn = task.getOrderSn();
        Integer status = orderFeign.getStatusBySn(orderSn);

        // 订单存在且状态不等于4和5就不用恢复库存
        if (status != null && !status.equals(4) && !status.equals(5)) return ;

        List<LockDetailTo> details = task.getDetails();
        List<Long> detailIds = details.stream().map(LockDetailTo::getId).collect(Collectors.toList());
        if (detailIds == null || detailIds.isEmpty()) return ;

        List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService
                .query()
                .in("id", detailIds)
                .eq("lock_status", 1)
                .list();
        if (detailEntities != null && !detailEntities.isEmpty()) {
            for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
                detailEntity.setLockStatus(2);
            }

            // 解锁库存更新task表单
            wareSkuDao.unlockStock(detailEntities);
            wareOrderTaskDetailService.updateBatchById(detailEntities);
        }
    }

    @Override
    @Transactional
    public void unlockStock(String orderSn) {
        Integer status = orderFeign.getStatusBySn(orderSn);

        // 订单存在且状态不等于4和5就不用恢复库存
        if (status != null && !status.equals(4) && !status.equals(5)) return ;

        Long taskId = wareOrderTaskService.getIdByOrderSn(orderSn);
        List<WareOrderTaskDetailEntity> detailEntities = wareOrderTaskDetailService
                .query()
                .eq("task_id", taskId)
                .eq("lock_status", 1)
                .list();
        if (detailEntities != null && !detailEntities.isEmpty()) {
            for (WareOrderTaskDetailEntity detailEntity : detailEntities) {
                detailEntity.setLockStatus(2);
            }

            // 解锁库存更新task表单
            wareSkuDao.unlockStock(detailEntities);
            wareOrderTaskDetailService.updateBatchById(detailEntities);
        }

    }
}