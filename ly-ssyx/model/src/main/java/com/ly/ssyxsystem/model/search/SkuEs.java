package com.ly.ssyxsystem.model.search;

import lombok.Data;

import java.util.List;

@Data
public class SkuEs {

    // 商品Id= skuId
    private Long id;

    private String keyword;

    private Integer skuType;

    private Integer isNewPerson;

    private Long categoryId;

    private String categoryName;

    private String imgUrl;

    //  es 中能分词的字段，这个字段数据类型必须是 text！keyword 不分词！
    private String title;

    private Double price;

    private Integer stock;

    private Integer perLimit;

    private Integer sale;

    private Long wareId;

    //  商品的热度！
    private Long hotScore = 0L;

    private List<String> ruleList;

}
