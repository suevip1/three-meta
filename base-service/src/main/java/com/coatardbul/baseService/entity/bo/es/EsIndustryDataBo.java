package com.coatardbul.baseService.entity.bo.es;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/13
 *
 * @author Su Xiaolei
 */
@Data
public class EsIndustryDataBo {


    private String id;

    private String yearStr;


    private String beginDateStr;

    private String endDateStr;

    private String dateStr;

    private String bkCode;

    private String bkName;

    private String increaseRate;

    private String maxIncreaseRate;

    private String openValue;

    private String maxValue;

    private String minValue;

    private String closeValue;

    private String lastCloseValue;


}
