package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
 * 智能ai策略
 */
@Data
public class StockTradeAiStrategy {
    private String id;

    /**
     * 策略名称
     */
    private String strategyName;

    /**
     * 策略标识
     */
    private String strategySign;

    /**
     * 是否打开交易
     */
    private Integer tradeFlag;

    /**
     * 参数对象
     */
    private String paramObject;

    /**
     * 说明
     */
    private String remark;
}