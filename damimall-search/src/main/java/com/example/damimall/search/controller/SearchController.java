package com.example.damimall.search.controller;

import com.example.damimall.search.service.MallSearchService;
import com.example.damimall.search.vo.SearchParam;
import com.example.damimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {
    @Autowired
    MallSearchService mallSearchService;

    @GetMapping("/list.html")
    public String toListHtml(SearchParam param, Model model, HttpServletRequest request){
        param.set_queryString(request.getQueryString());
        if (param.getPageNum() == null) param.setPageNum(1);
        SearchResult result = mallSearchService.search(param);

        model.addAttribute("result", result);
        return "list";
    }

}
