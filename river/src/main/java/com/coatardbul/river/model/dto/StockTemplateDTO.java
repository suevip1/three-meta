package com.coatardbul.river.model.dto;

import lombok.Data;

/**
    * 股票问句模板
    */
@Data
public class StockTemplateDTO {
    private String id;

    /**
    * 模板名称
    */
    private String name;

    /**
    * 模板表达式
    */
    private String scriptStr;

    /**
    * 表达式样例
    */
    private String exampleStr;

    /**
     * 当日日期指定
     */
    private String todayStr;

    /**
     * 题材指定指定
     */
    private String themeStr;

    /**
     * 备注
     */
    private String remark;


    /**
     * 备注
     */
    private String templateSign;
}