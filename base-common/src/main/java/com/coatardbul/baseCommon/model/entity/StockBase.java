package com.coatardbul.baseCommon.model.entity;

import lombok.Data;

/**
 * 股票基本信息
 */
@Data
public class StockBase {
    /**
     * 股票代码
     */
    private String code;

    /**
     * 股票名称
     */
    private String name;

    /**
     * 名称缩写
     */
    private String nameAbbr;

    /**
     * 同花顺所属行业
     */
    private String industry;

    /**
     * 概念
     */
    private String theme;
}