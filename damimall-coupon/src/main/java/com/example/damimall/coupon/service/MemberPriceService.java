package com.example.damimall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.common.utils.PageUtils;
import com.example.damimall.coupon.entity.MemberPriceEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品会员价格
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-08 00:00:09
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveBatchMember(List<MemberPriceEntity> memberPriceList);
}

