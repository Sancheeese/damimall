package com.example.damimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.damimall.product.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.ProductAttrValueDao;
import com.example.damimall.product.entity.ProductAttrValueEntity;
import com.example.damimall.product.service.ProductAttrValueService;


@Service("productAttrValueService")
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {
    @Autowired
    AttrService attrService;

    @Autowired
    ProductAttrValueDao productAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<ProductAttrValueEntity> page = this.page(
                new Query<ProductAttrValueEntity>().getPage(params),
                new QueryWrapper<ProductAttrValueEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveProductAttrValue(ProductAttrValueEntity productAttrValue) {
        save(productAttrValue);
    }

    @Override
    public void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> productAttrValues) {
        for (ProductAttrValueEntity productAttrValue : productAttrValues) {
            update(productAttrValue, new UpdateWrapper<ProductAttrValueEntity>()
                    .eq("spu_id", spuId)
                    .eq("attr_id", productAttrValue.getAttrId())
            );
        }
    }

    @Override
    public List<ProductAttrValueEntity> searchAvailableAttrs(Long spuId) {
        List<ProductAttrValueEntity> attrs = query().eq("spu_id", spuId).list();

        // 查询可被检索的属性
        List<Long> allIds = attrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        List<Long> searchAttrIds = attrService.getSearcIdsFormAllIds(allIds);
        Set<Long> set = new HashSet<>(searchAttrIds);

        List<ProductAttrValueEntity> searchAttrs = attrs.stream()
                .filter(attr -> set.contains(attr.getAttrId()))
                .collect(Collectors.toList());

        return searchAttrs;
    }

    @Override
    public List<String> getNameAndValue(Long skuId) {
        return productAttrValueDao.getNameAndValue(skuId);
    }

}