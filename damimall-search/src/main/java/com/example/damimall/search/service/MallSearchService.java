package com.example.damimall.search.service;

import com.example.damimall.search.vo.SearchParam;
import com.example.damimall.search.vo.SearchResult;

public interface MallSearchService {
    SearchResult search(SearchParam param);
}
