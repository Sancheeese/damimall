package com.example.damimall.product.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// import org.apache.shiro.authz.annotation.RequiresPermissions;
import com.example.damimall.product.entity.BrandEntity;
import com.example.damimall.product.service.BrandService;
import com.example.damimall.product.vo.BrandVo;
import com.example.damimall.product.vo.CategoryBrandRelationVo;
import org.bouncycastle.LICENSE;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.damimall.product.entity.CategoryBrandRelationEntity;
import com.example.damimall.product.service.CategoryBrandRelationService;
import com.example.common.utils.PageUtils;
import com.example.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author zhangshurui
 * @email 2689142369@qq.com
 * @date 2025-02-07 15:01:49
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private BrandService brandService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = categoryBrandRelationService.queryPage(params);

        return R.ok().put("page", page);
    }

    @RequestMapping("/brands/list")
    // @RequiresPermissions("product:categorybrandrelation:list")
    public R getBrandList(@RequestParam Long catId){
        List<CategoryBrandRelationEntity> categoryBrandRelation = categoryBrandRelationService.getBrandListByCatId(catId);
        List<CategoryBrandRelationVo> vos = new ArrayList<>();
        for (CategoryBrandRelationEntity en : categoryBrandRelation) {
            CategoryBrandRelationVo vo = new CategoryBrandRelationVo();
            BeanUtils.copyProperties(en, vo);
            vos.add(vo);
        }

        return R.ok().put("data", vos);
    }

    @RequestMapping("/catelog/list")
    public R getRelationCategory(@RequestParam Long brandId){
        List<CategoryBrandRelationEntity> data = categoryBrandRelationService.getRelationCategory(brandId);

        return R.ok().put("data", data);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    // @RequiresPermissions("product:categorybrandrelation:info")
    public R info(@PathVariable("id") Long id){
		CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    // @RequiresPermissions("product:categorybrandrelation:save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.saveRealtion(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    // @RequiresPermissions("product:categorybrandrelation:update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation){
		categoryBrandRelationService.updateById(categoryBrandRelation);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    // @RequiresPermissions("product:categorybrandrelation:delete")
    public R delete(@RequestBody Long[] ids){
		categoryBrandRelationService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
