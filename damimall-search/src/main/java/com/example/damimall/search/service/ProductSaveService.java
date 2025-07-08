package com.example.damimall.search.service;

import com.example.common.to.search.SkuEsTo;

import java.util.List;

public interface ProductSaveService {

    boolean productUp(List<SkuEsTo> products);

    boolean productUpdate(List<SkuEsTo> products);
}
