package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
 * 观察详情
 */
@Data
public class StockWatch {
    private String id;

    private String code;

    private String name;

    /**
     * 模板，策略标识
     */
    private String templateSign;

    /**
     * 交易数量
     */
    private String tradeNum;

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