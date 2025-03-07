package com.example.damimall.ware.service.impl;

import com.example.common.to.ware.SkuStockTo;
import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.ware.feign.ProductFeignService;
import com.example.damimall.ware.vo.StockUpdateVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {
    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    WareSkuDao wareSkuDao;

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
            BeanUtils.copyProperties(wareSkuEntity, skuStockTo);
            return skuStockTo;
        }).collect(Collectors.toList());

        return skuStockTos;
    }

}