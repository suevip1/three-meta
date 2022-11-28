package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
    * 交易配置表
    */
@Data
public class StockTradeConfig {
    private String id;

    /**
    * 交易类型：1.真实交易 2 模拟交易
    */
    private String simulateType;

    /**
    * 交易用户信息
    */
    private String userId;

    /**
    * 持仓id
    */
    private String assetId;

    /**
    * 标识
    */
    private String sign;
}