package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
    * 预交易详情
    */
@Data
public class StockPreTrade {
    private String id;

    private String code;

    private String name;

    /**
    * 交易日期 YYYY-MM-DD
    */
    private String tradeDate;

    /**
    * 预交易时间 HH:MM:SS
    */
    private String preTradeTime;

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

    /**
    * 交易状态：
    */
    private Integer status;
}