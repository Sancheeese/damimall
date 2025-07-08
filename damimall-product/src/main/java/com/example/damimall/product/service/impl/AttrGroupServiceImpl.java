package com.example.damimall.product.service.impl;

import com.baomidou.mybatisplus.extension.conditions.query.QueryChainWrapper;
import com.example.damimall.product.dao.AttrDao;
import com.example.damimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.damimall.product.entity.AttrEntity;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.AttrAttrgroupRelationService;
import com.example.damimall.product.service.AttrService;
import com.example.damimall.product.service.CategoryService;
import com.example.damimall.product.vo.AttrGroupWithAttrVo;
import com.example.damimall.product.vo.AttrVo;
import com.example.damimall.product.vo.itemVo.AttrGroupItemVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.AttrGroupDao;
import com.example.damimall.product.entity.AttrGroupEntity;
import com.example.damimall.product.service.AttrGroupService;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {
    @Autowired
    CategoryService categoryService;

    @Autowired
    AttrService attrService;

    @Autowired
    AttrAttrgroupRelationService attrAttrgroupRelationService;

    @Autowired
    AttrGroupDao attrGroupDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByCatId(Map<String, Object> params, Long catId) {
        IPage<AttrGroupEntity> pageParam = new Query<AttrGroupEntity>().getPage(params);
        QueryWrapper<AttrGroupEntity> queryWrapper = new QueryWrapper<AttrGroupEntity>();
        if (params.get("key") != null && !params.get("key").toString().isEmpty()){
            String key = params.get("key").toString();
            queryWrapper.and(qw -> qw.eq("attr_group_id", key).or().like("attr_group_name", key));
        }
        if (catId != 0){
            queryWrapper.eq("catelog_id", catId);
        }

        IPage<AttrGroupEntity> page = this.page(pageParam, queryWrapper);
        page.getRecords().stream().forEach(this::setCategoryPath);
        return new PageUtils(page);
    }

    @Override
    public AttrGroupEntity getInfo(Long attrGroupId) {
        AttrGroupEntity attrGroup = getById(attrGroupId);
        setCategoryPath(attrGroup);
        return attrGroup;
    }
;
    @Override
    public List<AttrGroupWithAttrVo> getAllGroupWithAttr(Long catId) {
        // 查出所有组
        List<AttrGroupEntity> attrGroups = query().eq("catelog_id", catId).list()
                .stream()
                .filter(group -> {
                    Integer count = attrAttrgroupRelationService.query()
                            .eq("attr_group_id", group.getAttrGroupId())
                            .count();
                    return count > 0;
                })
                .collect(Collectors.toList());
        List<AttrGroupWithAttrVo> vos = new ArrayList<>();
        if (attrGroups != null && !attrGroups.isEmpty())
            for (AttrGroupEntity attrGroup : attrGroups) {
                AttrGroupWithAttrVo vo = new AttrGroupWithAttrVo();
                BeanUtils.copyProperties(attrGroup, vo);

                List<AttrVo> attrVos = attrService.getRelation(attrGroup.getAttrGroupId());
                if (attrVos != null) {
                    vo.setAttrs(attrVos);
                }else{
                    vo.setAttrs(new ArrayList<AttrVo>());
                }
                vos.add(vo);
            }

        return vos;
    }

    @Override
    public List<AttrGroupItemVo> getAttrGroupWithAttr(Long spuId) {
        // 三表联查
        List<AttrGroupItemVo> attrGroupItemVos = attrGroupDao.getAttrGroupWithAttr(spuId);

        return attrGroupItemVos;
    }

    public void setCategoryPath(AttrGroupEntity ag){
        List<Long> path = new ArrayList<>();
        findAndSetPath(ag.getCatelogId(), path);
        ag.setCategoryPath(path);
    }

    public void findAndSetPath(Long catId, List<Long> path){
        if (catId == null || catId == 0) return ;
        Long parentId = categoryService
                .getOne(new QueryWrapper<CategoryEntity>().eq("cat_id", catId))
                .getParentCid();

        findAndSetPath(parentId, path);
        path.add(catId);
    }

}