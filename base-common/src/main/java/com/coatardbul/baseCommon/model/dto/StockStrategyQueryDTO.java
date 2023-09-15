package com.coatardbul.baseCommon.model.dto;

import lombok.Data;

/**
 * <p>
 * Note:支持两种模式
 * 1.传入id，日期，时间
 * 2.直接传入问句
 * <p>
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Data
public class StockStrategyQueryDTO {

    private String riverStockTemplateId;

    /**
     * 模板标识
     */
    private String riverStockTemplateSign;
    /**
     * 股票脚本
     */
    private String StockTemplateScript;
    /**
     * YYYY-MM-DD
     */
    private String dateStr;

    /**
     * 题材
     */
    private String themeStr;

    /**
     * HH:MM
     */
    private String timeStr;

    /**
     *股票代码
     */
    private String stockCode;

    private String stockName;
    /**
     * 查询字符串
     */
    private String queryStr;

    /**
     * 页码相关
     */
    private Integer pageSize;

    /**
     * 第几页
     */
    private Integer page;

    /**
     * 排序相关
     */
    private String orderStr;

    private String orderBy;


}
