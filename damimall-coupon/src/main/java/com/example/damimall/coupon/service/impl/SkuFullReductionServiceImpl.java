package com.example.damimall.coupon.service.impl;

import com.example.common.to.MemberPrice;
import com.example.common.to.SkuReductionTo;
import com.example.damimall.coupon.entity.MemberPriceEntity;
import com.example.damimall.coupon.entity.SkuLadderEntity;
import com.example.damimall.coupon.service.MemberPriceService;
import com.example.damimall.coupon.service.SkuLadderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.coupon.dao.SkuFullReductionDao;
import com.example.damimall.coupon.entity.SkuFullReductionEntity;
import com.example.damimall.coupon.service.SkuFullReductionService;
import org.springframework.transaction.annotation.Transactional;


@Service("skuFullReductionService")
public class SkuFullReductionServiceImpl extends ServiceImpl<SkuFullReductionDao, SkuFullReductionEntity> implements SkuFullReductionService {
    @Autowired
    SkuLadderService skuLadderService;

    @Autowired
    MemberPriceService memberPriceService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuFullReductionEntity> page = this.page(
                new Query<SkuFullReductionEntity>().getPage(params),
                new QueryWrapper<SkuFullReductionEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    @Transactional
    public void saveForFeign(SkuReductionTo skuReductionTo) {
        if (skuReductionTo.getFullCount() > 0) {
            SkuFullReductionEntity skuFullReductionEntity = new SkuFullReductionEntity();
            BeanUtils.copyProperties(skuReductionTo, skuFullReductionEntity);
            skuFullReductionEntity.setAddOther(skuReductionTo.getCountStatus());
            save(skuFullReductionEntity);
        }

        if (skuReductionTo.getFullPrice().compareTo(new BigDecimal(0)) > 0) {
            SkuLadderEntity skuLadderEntity = new SkuLadderEntity();
            BeanUtils.copyProperties(skuReductionTo, skuLadderEntity);
            skuLadderEntity.setAddOther(skuReductionTo.getPriceStatus());
            skuLadderService.save(skuLadderEntity);
        }

        List<MemberPriceEntity> memberPriceList = new ArrayList<>();
        skuReductionTo.getMemberPrice().stream()
                .filter(memberPrice -> memberPrice.getPrice().compareTo(new BigDecimal(0)) > 0)
                .forEach(memberPrice -> {
                    MemberPriceEntity memberPriceEntity = new MemberPriceEntity();
                    memberPriceEntity.setSkuId(skuReductionTo.getSkuId());
                    memberPriceEntity.setMemberLevelId(memberPrice.getId());
                    memberPriceEntity.setMemberLevelName(memberPrice.getName());
                    memberPriceEntity.setMemberPrice(memberPrice.getPrice());
                    memberPriceEntity.setAddOther(1);
                    memberPriceList.add(memberPriceEntity);
                });
        memberPriceService.saveBatchMember(memberPriceList);
    }

}