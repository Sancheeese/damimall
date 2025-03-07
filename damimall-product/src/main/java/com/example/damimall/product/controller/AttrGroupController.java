package com.example.damimall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.damimall.product.entity.AttrAttrgroupRelationEntity;
import com.example.damimall.product.service.AttrAttrgroupRelationService;
import com.example.damimall.product.service.AttrService;
import com.example.damimall.product.vo.AttrGroupWithAttrVo;
import com.example.damimall.product.vo.AttrVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.damimall.product.entity.AttrGroupEntity;
import com.example.damimall.product.service.AttrGroupService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 属性分组
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@RestController
@RequestMapping("product/attrgroup")
public class AttrGroupController {
    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private AttrAttrgroupRelationService attrAttrgroupRelationService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:attrgroup:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = attrGroupService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/list/{catId}")
    public R listByCatId(@RequestParam Map<String, Object> params, @PathVariable Long catId){
        PageUtils page = attrGroupService.queryPageByCatId(params, catId);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrGroupId}")
    // @RequiresPermissions("product:attrgroup:info")
    public R info(@PathVariable("attrGroupId") Long attrGroupId){
        AttrGroupEntity attrGroup = attrGroupService.getInfo(attrGroupId);

        return R.ok().put("attrGroup", attrGroup);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:attrgroup:save")
    public R save(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.save(attrGroup);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:attrgroup:update")
    public R update(@RequestBody AttrGroupEntity attrGroup){
		attrGroupService.updateById(attrGroup);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R delete(@RequestBody Long[] attrGroupIds){
		attrGroupService.removeByIds(Arrays.asList(attrGroupIds));

        return R.ok();
    }

    @RequestMapping("/{groupId}/attr/relation")
    public R getRelation(@PathVariable Long groupId){
        List<AttrVo> vos = attrService.getRelation(groupId);

        return R.ok().put("data", vos);
    }

    @RequestMapping("/{attrGroupId}/noattr/relation")
    public R getNoRelation(@RequestParam Map<String, Object> params, @PathVariable Long attrGroupId){
        PageUtils page = attrService.getNoRelation(params, attrGroupId);

        return R.ok().put("page", page);
    }

    @RequestMapping("/attr/relation")
    public R setRelation(@RequestBody List<AttrAttrgroupRelationEntity> relations){
        attrAttrgroupRelationService.saveRelation(relations);

        return R.ok();
    }

    @RequestMapping("/attr/relation/delete")
    // @RequiresPermissions("product:attrgroup:delete")
    public R batchDeleteRelation(@RequestBody List<AttrAttrgroupRelationEntity> relations){
        attrAttrgroupRelationService.batchDeleteRelation(relations);

        return R.ok();
    }

    @RequestMapping("/{catId}/withattr")
    public R getAllGroupWithAttr(@PathVariable Long catId){
        List<AttrGroupWithAttrVo> vos = attrGroupService.getAllGroupWithAttr(catId);

        return R.ok().put("data", vos);
    }



}
