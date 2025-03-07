package com.example.damimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.AttrAttrgroupRelationDao;
import com.example.damimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.damimall.product.service.AttrAttrgroupRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveRelation(List<AttrAttrgroupRelationEntity> relations) {
        for (AttrAttrgroupRelationEntity aar : relations){
            aar.setAttrSort(0);
        }
        saveBatch(relations);
    }

    @Override
    @Transactional
    public void batchDeleteRelation(List<AttrAttrgroupRelationEntity> relations) {
        List<Long> attrIds = new ArrayList<>();
        for (AttrAttrgroupRelationEntity relation : relations){
            attrIds.add(relation.getAttrId());
        }
        Long groupId = relations.get(0).getAttrGroupId();

        // 批量删除
        int batchSize = 1000;
        for (int i = 0; i < attrIds.size(); i += batchSize) {
            int toIndex = Math.min(i + batchSize, attrIds.size());
            List<Long> deleteIds = attrIds.subList(i, toIndex);

            this.remove(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_group_id", groupId)
                    .in("attr_id", deleteIds));
        }
    }

}