package com.coatardbul.baseCommon.model.bo.trade;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/10/23
 *
 * @author Su Xiaolei
 */
@Data
public class StockDetail {

    /**
     * 时间
     */
    private String dateStr;

    private String code;

    private String name;

    private String codeUrl;

    private String objectEnumSign;

    /**
     * 涨停封单范围
     */
    private String upLimitVolRange;

    private String upLimitMixSubVolRange;

    private String upLimitMaxSubVolRange;

    /**
     * 昨日竞价涨幅
     */
    private BigDecimal lastAuctionIncreaseRate;
    /**
     * 昨日最新涨幅
     */
    private BigDecimal lastNewIncreaseRate;

    /**
     * 昨日涨幅差值
     */
    private BigDecimal lastSubIncreaseRate;


    /**
     * 涨停打开次数
     */
    private String upLimitOpenNum;


    private String upLimitDetail;


    /**
     * 竞价涨幅
     */
    private  BigDecimal auctionIncreaseRate;


    /**
     * 最新涨幅
     */
    private BigDecimal newIncreaseRate;

    /**
     * 涨幅差值
     */
    private BigDecimal subIncreaseRate;


    /**
     * 竞价交易金额
     */
    private BigDecimal auctionTradeAmount;
    /**
     * 交易金额
     */
    private BigDecimal tradeAmount;
    /**
     * 竞价换手率
     */
    private BigDecimal auctionTurnOverRate;
    /**
     * 竞价量比
     */
    private BigDecimal auctionVol;
    /**
     * 换手率
     */
    private BigDecimal turnOverRate;


    private  BigDecimal marketValue;

    private  BigDecimal circulationMarketValue;


    private  BigDecimal newPrice;

    /**
     * 题材
     */
    private String  theme;


    /**
     * 昨日竞价交易金额
     */
    private BigDecimal  lastAuctionTradeAmount;


    /**
     * 昨日交易金额
     */
    private BigDecimal  lastTradeAmount;

    /**
     * 11
     */
    private BigDecimal avgPriceRate;

    /**
     * 集中度90
     */
    private BigDecimal concentrationRatio;



    private String firstUplimitTime;


    /**
     * 收盘获利
     */
    private BigDecimal earnProfit;



    private String ma5;
    private String ma10;
    private String ma20;
    private String ma45;
    private String ma90;
    private String ma180;
    private String ma360;


}
