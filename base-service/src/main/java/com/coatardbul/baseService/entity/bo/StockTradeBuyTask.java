package com.coatardbul.baseService.entity.bo;

import lombok.Data;

/**
    * 定时任务交易计划
    */
@Data
public class StockTradeBuyTask {
    private String id;

    /**
    * 策略标识
    */
    private String strategySign;

    /**
    * 策略名称
    */
    private String strategyName;

    /**
    * 策略参数，根据默认值来修改
    */
    private String strategyParam;

    /**
    * 定时任务名称，唯一性 表名+id或者表业务+id
    */
    private String jobName;

    /**
    * 表达式
    */
    private String cron;

    /**
    * 交易金额，交易金额优先
    */
    private String tradeAmount;

    /**
    * 股票代码
    */
    private String stockCode;

    /**
    * 股票名称
    */
    private String stockName;

    /**
    * 交易数量
    */
    private String tradeNum;

    /**
    * 交易价格
    */
    private String tradePrice;

    /**
     * 交易用户id
     */
    private String tradeUserId;
}