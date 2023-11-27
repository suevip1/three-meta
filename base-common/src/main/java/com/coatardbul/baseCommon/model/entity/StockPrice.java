package com.coatardbul.baseCommon.model.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class StockPrice {


    private String id;

    /**
     * 编码
     */
    private String code;

    /**
     * 日期
     */
    private String dateStr;

    /**
     * 名称
     */
    private String name;




    /**
     * 开盘价
     */
    private BigDecimal openPrice;


    /**
     * 当前涨幅
     */
    private BigDecimal currIncreaseRate;

    /**
     * 收盘价
     */
    private BigDecimal closePrice;

    /**
     * 最低价
     */
    private BigDecimal minPrice;

    /**
     * 最高价
     */
    private BigDecimal maxPrice;

    /**
     * 平开价（即昨日收盘价）
     */
    private BigDecimal lastClosePrice;

    /**
     * 换手率
     */
    private BigDecimal turnOverRate;

    /**
     * 量比
     */
    private BigDecimal quantityRelativeRatio;

    /**
     * 量
     */
    private Integer volume;


    private BigDecimal tradeAmount;

    /**
     * 振幅
     */
    private BigDecimal maxSubRate;


}