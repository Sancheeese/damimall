package com.example.damimall.product.service.impl;

import com.example.common.constant.ProductConstant;
import com.example.common.to.search.SkuEsTo;
import com.example.common.utils.ObjectMapperUtils;
import com.example.common.utils.RedisLogicData;
import com.example.damimall.product.dao.CategoryBrandRelationDao;
import com.example.damimall.product.dao.SkuInfoDao;
import com.example.damimall.product.feign.SearchFeignService;
import com.example.damimall.product.service.CategoryBrandRelationService;
import com.example.damimall.product.utils.SimpleRedisLock;
import com.example.damimall.product.vo.webVo.Category2LevelVo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.common.utils.PageUtils;
import com.example.common.utils.Query;

import com.example.damimall.product.dao.CategoryDao;
import com.example.damimall.product.entity.CategoryEntity;
import com.example.damimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {
    @Autowired
    CategoryBrandRelationDao categoryBrandRelationDao;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SearchFeignService searchFeignService;

    @Autowired
    SkuInfoDao skuInfoDao;

    private static final ExecutorService REBUILD_REDIS = Executors.newFixedThreadPool(10);

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
        if (category.getSort() == null) category.setSort(0);
        save(category);
    }

    @Override
    @CacheEvict(value = "product", key = "'firstLevel'")
//    @Caching(evict = {
//            @CacheEvict(),
//            @CacheEvict()
//    })
    @Transactional
    public void updateCat(CategoryEntity category) {
        // 更新自己的表
        updateById(category);

        // 更新与之相关的表
        categoryBrandRelationDao.updateCatName(category.getCatId(), category.getName());

        // 更新es
        List<Long> skuIds = skuInfoDao.getSkuIdsByCatId(category.getCatId());
        List<SkuEsTo> skuEsTos = skuIds.stream().map(id -> {
            SkuEsTo skuEsTo = new SkuEsTo();
            skuEsTo.setSkuId(id);
            skuEsTo.setCatelogName(category.getName());
            return skuEsTo;
        }).collect(Collectors.toList());
        searchFeignService.updateProduct(skuEsTos);

    }

    @Cacheable(value = "product", key = "'firstLevel'")
    @Override
    public List<CategoryEntity> getFirstLevelCategory() {
        return query().eq("parent_cid", 0).list();
    }

    @Override
    public Map<String, List<Category2LevelVo>> getCatalogJson() {
        Map<String, List<Category2LevelVo>> firstLevel = null;
        SimpleRedisLock redisLock = new SimpleRedisLock(ProductConstant.PRODUCT_3LEVEL_LOCK, redisTemplate);
        String retJson = redisTemplate.opsForValue().get(ProductConstant.PRODUCT_3LEVEL_CAT_CACHE_KEY);
        String uuid = UUID.randomUUID().toString();
        // 查缓存
        if (retJson != null){
            if (retJson.equals(ProductConstant.PRODUCT_3LEVEL_EMPTY_VALUE)) return null;
            RedisLogicData<Map<String, List<Category2LevelVo>>> redisLogicData =
                    ObjectMapperUtils.readValue(retJson, new TypeReference<RedisLogicData<Map<String, List<Category2LevelVo>>>>() {});

            LocalDateTime expireTime = redisLogicData.getExpireTime();
            firstLevel = redisLogicData.getData();
            // 过期了需要重新更新缓存
            try {
                if (expireTime.isBefore(LocalDateTime.now()) && redisLock.tryLock(10L, uuid)) {
                    // 开启一个新线程执行更新操作
                    REBUILD_REDIS.submit(() -> {
                        try {
                            Map<String, List<Category2LevelVo>> catalogs = getCatalogJsonFromDb();
                            save2Redis(catalogs, ProductConstant.PRODUCT_3LEVEL_CAT_CACHE_KEY, 3000L);
                        } finally {
                            redisLock.unLock(uuid);
                        }
                    });
                }
            } finally {
                redisLock.unLock(uuid);
            }
            return firstLevel;
        }

        firstLevel = getCatalogJsonFromDb();
        if (firstLevel == null || firstLevel.isEmpty()) {
//            空值存进去
            redisTemplate.opsForValue().set(ProductConstant.PRODUCT_3LEVEL_CAT_CACHE_KEY,
                    ProductConstant.PRODUCT_3LEVEL_EMPTY_VALUE, 2, TimeUnit.MINUTES);
        }else{
//            存到缓存
            save2Redis(firstLevel, ProductConstant.PRODUCT_3LEVEL_CAT_CACHE_KEY, 3000L);
        }

        return firstLevel;
    }

    public Map<String, List<Category2LevelVo>> getCatalogJsonFromDb() {
        List<CategoryEntity> allCategory = query().list();
        Map<String, List<Category2LevelVo>> firstLevel = new HashMap<>();
        Map<String, List<Category2LevelVo.Category3LevelVo>> secondLevel = new HashMap<>();

        for (CategoryEntity categoryEntity : allCategory) {
            Integer level = categoryEntity.getCatLevel();
            Long id = categoryEntity.getCatId();
            Long parentId = categoryEntity.getParentCid();
            String name = categoryEntity.getName();
            if (level.equals(1)){
                if (!firstLevel.containsKey(id.toString()))
                    firstLevel.put(id.toString(), new ArrayList<>());
            }else if (level.equals(2)){
                if (!firstLevel.containsKey(parentId.toString())){
                    firstLevel.put(parentId.toString(), new ArrayList<>());
                }else{
                    firstLevel.get(parentId.toString()).add(new Category2LevelVo(parentId, id, name));
                }
            }else if (level.equals(3)){
                if (!secondLevel.containsKey(parentId.toString())){
                    secondLevel.put(parentId.toString(), new ArrayList<>());
                }else{
                    secondLevel.get(parentId.toString()).add(new Category2LevelVo.Category3LevelVo(parentId, id, name));
                }
            }
        }

        for (List<Category2LevelVo> category2LevelVos : firstLevel.values()) {
            for (Category2LevelVo category2LevelVo : category2LevelVos) {
                List<Category2LevelVo.Category3LevelVo> vos = secondLevel.get(category2LevelVo.getId().toString());
                category2LevelVo.setCatalog3List(vos);
            }
        }

        return firstLevel;
    }

    public <T> void save2Redis(T data, String key, Long expireSeconds){
        RedisLogicData<T> redisData = new RedisLogicData<>();
        redisData.setData(data);
        redisData.setExpireTime(LocalDateTime.now().plusSeconds(expireSeconds));

        redisTemplate.opsForValue().set(key, ObjectMapperUtils.writeValueAsString(redisData));

    }

}