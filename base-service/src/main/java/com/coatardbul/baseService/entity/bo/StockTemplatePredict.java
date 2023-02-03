package com.coatardbul.baseService.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 股票模型预测表
 */
@Data
public class StockTemplatePredict {
    private String id;

    /**
     * 股票代码
     */
    private String code;

    private String name;

    /**
     * 市值
     */
    private BigDecimal buyMarketValue;

    /**
     * 换手率
     */
    private BigDecimal buyTurnoverRate;

    /**
     * 买入时交易金额
     */
    private BigDecimal buyTradeAmount;

    /**
     * YYYY-MM-DD日期
     */
    private String date;

    /**
     * 模板id
     */
    private String templatedId;
    private String templatedName;

    /**
     * 模板标识
     */
    private String templatedSign;

    /**
     * 买入时间 年月日时分
     */
    private String buyTime;

    /**
     * 买入价格
     */
    private BigDecimal buyPrice;

    /**
     * 买入集合竞价涨幅
     */
    private BigDecimal buyAuctionIncreaseRate;

    /**
     * 买入涨幅比例
     */
    private BigDecimal buyIncreaseRate;

    /**
     * 收盘涨幅
     */
    private BigDecimal buyCloseIncreaseRate;

    /**
     * 持有天数
     */
    private Integer holdDay;

    /**
     * 卖出时间，年月日时分
     */
    private String saleTime;

    /**
     * 卖出集合竞价涨幅
     */
    private BigDecimal saleAuctionIncreaseRate;

    /**
     * 卖出时涨幅
     */
    private BigDecimal saleIncreaseRate;

    /**
     * 卖出时收盘涨幅
     */
    private BigDecimal saleCloseIncreaseRate;

    /**
     * 卖出价格
     */
    private BigDecimal salePrice;

    /**
     * 具体详情，保留字段
     */
    private String detail;

    /**
     * 行业
     */
    private String industry;

    /**
     * 概念
     */
    private String concept;

    /**
     * 筹码集中度
     */
    private String concentrationRatio;

    /**
     * 获利比例
     */
    private String earnProfit;

    /**
     * 筹码成本
     */
    private BigDecimal jettonCost;

    /**
     * 上次集中度
     */
    private String lastConcentrationRatio;

    /**
     * 标记状态：目前只做标记使用
     */
    private Integer status;
}