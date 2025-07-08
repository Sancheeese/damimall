package com.example.damimall.ware.service.impl;

import com.example.common.utils.ParamUtils;
import com.example.common.utils.R;
import com.example.damimall.ware.feign.MemberFeign;
import com.example.damimall.ware.vo.FareVo;
import com.example.damimall.ware.vo.MemberAddressVo;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.ware.dao.WareInfoDao;
import com.example.damimall.ware.entity.WareInfoEntity;
import com.example.damimall.ware.service.WareInfoService;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {
    @Autowired
    MemberFeign memberFeign;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                new QueryWrapper<WareInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listByCondition(Map<String, Object> params) {
        QueryWrapper<WareInfoEntity> queryWrapper = new QueryWrapper<>();

        if (!ParamUtils.isNullOrEmpty(params, "key")){
            String key = (String) params.get("key");
            queryWrapper.eq("id", key).or().like("name", key);
        }

        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public FareVo getFare(Long addressId) {
        R r = memberFeign.info(addressId);
        MemberAddressVo memberAddressVo = r.getWithKey("memberReceiveAddress", new TypeReference<MemberAddressVo>() {});
        if (memberAddressVo != null) {
            FareVo fareVo = new FareVo();
            fareVo.setMemberAddressVo(memberAddressVo);

            // 计算运费
//            int fare = 10 + new Random().nextInt(20);
            fareVo.setFare(new BigDecimal(20));

            return fareVo;
        }

        return null;
    }

}