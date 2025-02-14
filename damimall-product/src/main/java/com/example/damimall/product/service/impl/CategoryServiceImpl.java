package com.example.damimall.product.service.impl;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.CategoryDao;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> treeList() {
        List<CategoryEntity> allCategory = list();

        for (CategoryEntity c : allCategory){
            if (c.getSort() == null) System.out.println(c.getCatId() + c.getName());
        }

        List<CategoryEntity> topCategory = allCategory.stream()
                .filter(c -> c.getParentCid().equals(0L))
                .map(top -> {
                    top.setChildren(getChildren(top, allCategory));
                    return top;
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());

        return topCategory;
    }



    public List<CategoryEntity> getChildren(CategoryEntity curr, List<CategoryEntity> all){
        List<CategoryEntity> children = all.stream()
                .filter(c -> c.getParentCid().equals(curr.getCatId()))
                .map(child ->{
                    child.setChildren(getChildren(child, all));
                    return child;
                })
                .sorted(Comparator.comparingInt(CategoryEntity::getSort))
                .collect(Collectors.toList());

        return children;
    }

    @Override
    public void removeMenuByIds(List<Long> asList) {
        // TODO 检查要删除的分类还有没有在别的地方的引用
        removeByIds(asList);
    }

    @Override
    public void saveOne(CategoryEntity category) {
        if (category.getProductCount() == null) category.setProductCount(0);
        save(category);
    }


}