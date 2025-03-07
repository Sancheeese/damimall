package com.example.damimall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.common.constant.ProductConstant;
import com.example.damimall.product.entity.*;
import com.example.damimall.product.service.*;
import com.example.damimall.product.vo.AttrVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.AttrDao;
import com.example.damimall.product.entity.AttrEntity;
import org.springframework.transaction.annotation.Transactional;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {
    @Autowired
    AttrGroupService attrGroupService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    CategoryService categoryService;

    @Autowired
    ProductAttrValueService productAttrValueService;

    @Autowired
    AttrDao attrDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils listAttr(String type, Map<String, Object> params, Long catlogId) {
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<>();

        if (catlogId != null && catlogId > 0){
            queryWrapper.eq("catelog_id", catlogId);
        }

        if ("base".equals(type)) queryWrapper
                .and(qw -> qw.eq("attr_type", 2).or().eq("attr_type", 1));
        else if ("sale".equals(type)) queryWrapper.
                and(qw -> qw.eq("attr_type", 2).or().eq("attr_type", 0));

        if (params.get("key") != null && !params.get("key").toString().isEmpty()){
            String key = params.get("key").toString();
            queryWrapper.and(qw -> qw.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), queryWrapper);
        PageUtils pageUtils = new PageUtils(page);

        // entity转vo
        List<AttrEntity> records = page.getRecords();
        List<AttrVo> vos = new ArrayList<>();
        Map<Long, String> groupId2Name = new HashMap<>();
        Map<Long, String> catId2Name = new HashMap<>();
        for (AttrEntity attrEntity : records){
            AttrVo attrVo = new AttrVo();
            BeanUtils.copyProperties(attrEntity, attrVo);

            // 查询组名
            AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.query()
                    .eq("attr_id", attrEntity.getAttrId())
                    .one();
            if (relation != null) {
                Long groupId = relation.getAttrGroupId();
                if (!groupId2Name.containsKey(groupId)) {
                    String groupName = attrGroupService.query()
                            .eq("attr_group_id", groupId)
                            .one()
                            .getAttrGroupName();

                    attrVo.setGroupName(groupName);
                    groupId2Name.put(groupId, groupName);
                }else{
                    attrVo.setGroupName(groupId2Name.get(groupId));
                }
            }

            // 查询分类名
            Long catId = attrEntity.getCatelogId();
            if (!catId2Name.containsKey(catId)) {
                String catName = categoryService.query()
                        .eq("cat_id", catId)
                        .one()
                        .getName();
                attrVo.setCatelogName(catName);
                catId2Name.put(catId, catName);
            }else{
                attrVo.setCatelogName(catId2Name.get(catId));
            }

            // 添加到list
            vos.add(attrVo);
        }

        pageUtils.setList(vos);
        return pageUtils;
    }

    @Transactional
    @Override
    public void saveAttr(AttrVo attrVo) {
        // 保存自己的表
        AttrEntity attr = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attr);
        save(attr);

        if (attrVo.getGroupId() != null && attrVo.getAttrType() == ProductConstant.AttrType.ATTR_TYPE_BASE.getCode()) {
            // 保存相关的表，关联的分组和类别
            AttrAttrgroupRelationEntity attr2GroupRelation = new AttrAttrgroupRelationEntity();
            attr2GroupRelation.setAttrId(attr.getAttrId());
            attr2GroupRelation.setAttrGroupId(attrVo.getGroupId());
            attr2GroupRelation.setAttrSort(0);

            attrAttrgroupRelationService.save(attr2GroupRelation);
        }
    }

    @Override
    @Transactional
    public void updateAttr(AttrVo attrVo) {
        // 更新自己的表
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attrVo, attrEntity);
        updateById(attrEntity);

        if (attrVo.getGroupId() != null) {
            // 更新属性与组的关联
            AttrAttrgroupRelationEntity relation = new AttrAttrgroupRelationEntity();
            relation.setAttrId(attrVo.getAttrId());
            relation.setAttrGroupId(attrVo.getGroupId());

            // 先判断之前有没有关联组
            int count = attrAttrgroupRelationService.count(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attrVo.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationService.update(relation, new UpdateWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrVo.getAttrId()));
            } else {
                relation.setAttrSort(0);
                attrAttrgroupRelationService.save(relation);
            }
        }
    }

    @Override
    @Transactional
    public void deleteAttr(Long[] attrIds) {
        List<Long> idList = Arrays.asList(attrIds);
        removeByIds(idList);

        // 删除关联表的数据
        int subSize = 1000;
        for (int i = 0; i < idList.size(); i++){
            int toIdx = Math.min(i + subSize, idList.size());
            List<Long> batchIds = idList.subList(i, toIdx);
            attrAttrgroupRelationService.remove(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .in("attr_id", batchIds));
        }
    }

    @Override
    public AttrVo info(Long attrId) {
        // 查attrEntity
        AttrEntity attrEntity = this.getById(attrId);
        AttrVo attrVo = new AttrVo();
        BeanUtils.copyProperties(attrEntity, attrVo);

        // 查catelogPath
        setCategoryPath(attrVo);

        // 根据attrId查group信息
        AttrAttrgroupRelationEntity relation = attrAttrgroupRelationService.query()
                .eq("attr_id", attrId).one();

        if (relation != null) {
            Long groupId = relation.getAttrGroupId();
            String groupName = attrGroupService.getById(groupId).getAttrGroupName();
            attrVo.setGroupId(groupId);
            attrVo.setGroupName(groupName);
        }

        return attrVo;
    }



    public void setCategoryPath(AttrVo attrVo){
        List<Long> path = new ArrayList<>();
        findAndSetPath(attrVo.getCatelogId(), path);
        attrVo.setCatelogPath(path);
    }

    public void findAndSetPath(Long catId, List<Long> path){
        if (catId == null || catId == 0) return ;
        Long parentId = categoryService
                .getOne(new QueryWrapper<CategoryEntity>().eq("cat_id", catId))
                .getParentCid();

        findAndSetPath(parentId, path);
        path.add(catId);
    }


    @Override
    public PageUtils getNoRelation(Map<String, Object> params, Long attrGroupId) {
        // 本分类id
        AttrGroupEntity theGroup = attrGroupService.getById(attrGroupId);
        Long theCatId = theGroup.getCatelogId();

        // 本分类下所有的组
        List<AttrGroupEntity> allGroup = attrGroupService.query()
                .eq("catelog_id", theCatId)
                .list();
        List<Long> allGroupIds = allGroup.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        // 本分类下已分类属性
        List<AttrAttrgroupRelationEntity> allocatedAttr = attrAttrgroupRelationService.query()
                .in("attr_group_id", allGroupIds)
                .list();
        List<Long> allocatedIds = allocatedAttr.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        // 本分类下未分配的属性
        QueryWrapper<AttrEntity> queryWrapper = new QueryWrapper<AttrEntity>()
                .eq("catelog_id", theCatId)
                .eq("attr_type", ProductConstant.AttrType.ATTR_TYPE_BASE.getCode());
        if (allocatedIds != null && !allocatedIds.isEmpty()) {
            queryWrapper.notIn("attr_id", allocatedIds);
        }

        if (params.get("key") != null){
            String key = params.get("key").toString();
            queryWrapper.and(qw -> qw.eq("attr_id", key).or().like("attr_name", key));
        }

        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

//    获取与分组相关联的属性
    @Override
    public List<AttrVo> getRelation(Long groupId) {
        List<Long> attrIds = attrAttrgroupRelationService.query()
                .eq("attr_group_id", groupId)
                .list()
                .stream()
                .map(AttrAttrgroupRelationEntity::getAttrId)
                .collect(Collectors.toList());

        List<AttrVo> vos = null;
        if (attrIds != null && !attrIds.isEmpty()) {
            vos = listByIds(attrIds)
                    .stream()
                    .map((attrEntity -> {
                        AttrVo attrVo = new AttrVo();
                        BeanUtils.copyProperties(attrEntity, attrVo);
                        return attrVo;
                    }))
                    .collect(Collectors.toList());
        }

        return vos;
    }

    @Override
    public List<ProductAttrValueEntity> getBaseAttrBySpuId(Long spuId) {
        List<ProductAttrValueEntity> attrValues = productAttrValueService.query()
                .eq("spu_id", spuId).list();
        return attrValues;
    }

    @Override
    public List<Long> getSearcIdsFormAllIds(List<Long> allIds) {
        List<Long> searchIds = attrDao.getSearchIdsFormAllIds(allIds);
        return searchIds;
    }

}