package com.coatardbul.baseCommon.model.bo.trade;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note: 单个stock基本详情
 * <p>
 * Date: 2022/8/5
 *
 * @author Su Xiaolei
 */
@Data
public class StockBaseDetail {


    private String code;

    private String name;


    /**
     * 市值
     */
    private BigDecimal marketValue;

    /**
     * 换手率
     */
    private BigDecimal turnOverRate;


    /**
     *交易金额
     */
    private BigDecimal tradeAmount;

    /**
     * 昨日收盘价
     */
    private BigDecimal lastClosePrice;

    /**
     * 当日涨停价
     */
    private BigDecimal upLimitPrice;

    /**
     * 当日跌停价
     */
    private BigDecimal downLimitPrice;

    /**
     * 集合竞价涨幅
     */
    private BigDecimal callAuctionRate;

    /**
     * 集合竞价涨幅
     */
    private BigDecimal callAuctionPrice;
    /**
     * 目前最新涨幅
     */
    private BigDecimal currUpRate;

    /**
     * 目前价格
     */
    private BigDecimal currPrice;

    /**
     * 建议买入涨幅
     */
    private BigDecimal sugBuyRate;

    /**
     * 建议买入价格
     */
    private BigDecimal sugBuyPrice;
    /**
     * 建议卖出涨幅
     */
    private BigDecimal sugSellRate;

    /**
     * 建议卖出价格
     */
    private BigDecimal sugSellPrice;

    /**
     * 最大涨幅
     */
    private BigDecimal maxUpRate;

    /**
     * 最大价
     */
    private BigDecimal maxPrice;
    /**
     * 最低价对应的涨幅
     */
    private BigDecimal minUpRate;

    /**
     * 最低价对应的涨幅
     */
    private BigDecimal minPrice;

    /**
     * 最低价对应的涨幅
     */
    private BigDecimal fiveIncreaseRate;

    /**
     * 同花顺所属行业
     */
    private String industry;

    /**
     * 概念
     */
    private String theme;


}
