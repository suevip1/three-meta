package com.coatardbul.river.model.entity;

import lombok.Data;

/**
 * 股票问句模板
 */
@Data
public class StockQueryTemplate {
    private String id;

    /**
     * 模板名称
     */
    private String name;

    /**
     * 模板表达式
     */
    private String scriptStr;

    /**
     * 表达式样例
     */
    private String exampleStr;

    /**
     * 热力值
     */
    private Integer hotValue;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 指定当日的字符串，和模板表达式匹配
     */
    private String todayStr;

    /**
     * 英文标识，用来识别模板，不用id识别，不能重复
     */
    private String templateSign;

    /**
     * 指定题材信息，和模板匹配
     */
    private String themeStr;
}