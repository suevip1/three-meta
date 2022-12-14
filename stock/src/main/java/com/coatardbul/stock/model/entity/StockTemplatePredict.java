package com.coatardbul.stock.model.entity;

import java.math.BigDecimal;
import lombok.Data;

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
     * YYYY-MM-DD日期
     */
    private String date;

    /**
     * 模板id
     */
    private String templatedId;
    private String templatedName;

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
     * 买入涨幅比例
     */
    private BigDecimal buyIncreaseRate;

    /**
     * 收盘涨幅
     */
    private BigDecimal closeIncreaseRate;

    /**
     * 持有天数
     */
    private Integer holdDay;

    /**
     * 卖出时间，年月日时分
     */
    private String saleTime;

    /**
     * 卖出价格
     */
    private BigDecimal salePrice;

    /**
     * 市值
     */
    private BigDecimal marketValue;

    /**
     * 具体详情
     */
    private String detail;

    /**
     * 换手率
     */
    private BigDecimal turnoverRate;

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
}