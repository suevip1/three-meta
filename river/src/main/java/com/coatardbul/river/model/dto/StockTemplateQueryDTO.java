package com.coatardbul.river.model.dto;

import lombok.Data;

/**
    * 股票问句模板
    */
@Data
public class StockTemplateQueryDTO {
    private String id;
    /**
     * HH:mm
     */
    private String  timeStr;


    private String objectSign;

    private String stockCode;

    private String themeStr;
    /**
    * 模板名称
    */
    private String name;

    /**
     * 当前问句查询日期YYYY-MM-DD
     */
    private String dateStr;


    /**
     * 股票脚本
     */
    private String stockScript;

}