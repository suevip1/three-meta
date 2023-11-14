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

    private String dateStr;

    private String bkCode;

    private String bkName;

    private String increaseRate;

    private String maxIncreaseRate;
}
