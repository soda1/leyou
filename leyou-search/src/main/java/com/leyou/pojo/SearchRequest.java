package com.leyou.pojo;

import java.util.Map;

public class SearchRequest {

    private String key;

    private Integer page;

    private String sortBy;

    private Boolean descending;

    private Map<String, String> filter;

    private static final Integer DEFAULT_SIZE = 20;

    private static final Integer DEFAULT_PAGE = 1;


    public Map<String, String> getFilter() {
        return filter;
    }


    public void setFilter(Map<String, String> filter) {
        this.filter = filter;
    }


    public String getSortBy() {
        return sortBy;
    }


    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }


    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }



    public String getKey() {
        return key;
    }


    public void setKey(String key) {
        this.key = key;
    }


    public Integer getPage() {
        if(page == null){
            return DEFAULT_PAGE;
        }
        // 获取页码时做一些校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }


    public void setPage(Integer page) {
        this.page = page;
    }


    public static Integer getDefaultSize() {
        return DEFAULT_SIZE;
    }


    public static Integer getDefaultPage() {
        return DEFAULT_PAGE;
    }



    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
