package com.coatardbul.baseService.entity.bo.es;

import lombok.Data;

/**
 * <p>
 * Note:Note:es上同步模板数据,日期+模板id+code,模板id，日期，code ,name,概念，行业前缀，行业，参数（保留字段），数据json
 * <p>
 * Date: 2023/11/11
 *
 * @author Su Xiaolei
 */
@Data
public class EsTemplateDataBo {

    /**
     * 日期+模板id+code
     */
    private String id;


    private String templateId;


    private String dateStr;


    private String stockCode;

    private String stockName;
    /**
     * 同花顺所属行业
     */
    private String industry;

    private String industryPrefix;
    /**
     * 概念
     */
    private String theme;


    /**
     * 参数
     */
    private String params;


    private String jsonStr;
}
