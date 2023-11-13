package com.coatardbul.baseCommon.model.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/13
 *
 * @author Su Xiaolei
 */
@Data
public class EsTemplateConfigDTO extends  StockStrategyQueryDTO{
    /**
     * 数据类型：每日数据day 竞价数据auction 分钟数据minuter
     */
    @NotBlank(message = " 数据类型不能为空")
    private String esDataType;

    /**
     * 取数方式：第一页first，全部数据all
     */
    @NotBlank(message = "取数方式不能为空")
    private String esDataMode;

    /**
     * 数据级别：small,middle(默认),big
     */
    @NotBlank(message = "数据级别不能为空")
    private String esDataLevel;

    /**
     * es索引名称
     */
    @NotBlank(message = "es索引名称不能为空")
    private String esIndexName;


}
