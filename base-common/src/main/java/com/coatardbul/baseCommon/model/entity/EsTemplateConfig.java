package com.coatardbul.baseCommon.model.entity;

import lombok.Data;

/**
 * es模板配置表
 */
@Data
public class EsTemplateConfig {
    private String id;

    /**
     * 模板id
     */
    private String templateId;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 数据类型：每日数据day 竞价数据auction 分钟数据minuter
     */
    private String esDataType;

    /**
     * 取数方式：第一页first，全部数据all
     */
    private String esDataMode;

    /**
     * 数据级别：small,middle(默认),big
     */
    private String esDataLevel;

    /**
     * es索引名称
     */
    private String esIndexName;

    /**
     * 顺序
     */
    private Integer sequent;
}