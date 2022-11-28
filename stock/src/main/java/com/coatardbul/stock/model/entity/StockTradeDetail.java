package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
 * 交易详情表
 */
@Data
public class StockTradeDetail {
    private String id;

    private String code;

    private String name;

    /**
     * 买入还是卖出
     */
    private String tradeType;

    /**
     * 模拟类型，真实还是模拟
     */
    private String simulateType;

    /**
     * 交易日期 YYYY-MM-DD
     */
    private String tradeDate;

    /**
     * 交易时间 HH:MM
     */
    private String tradeTime;

    /**
     * 委托数量 以100 为单位
     */
    private String delegateNum;

    /**
     * 交易数量
     */
    private String tradeNum;

    /**
     * 委托价格
     */
    private String delegatePrice;

    /**
     * 交易价格
     */
    private String tradePrice;

    /**
     * 交易总金额
     */
    private String tradeAllAmount;

    /**
     * 信息
     */
    private String msg;
}