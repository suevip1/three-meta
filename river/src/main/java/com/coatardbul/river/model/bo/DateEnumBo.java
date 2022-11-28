package com.coatardbul.river.model.bo;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/28
 *
 * @author Su Xiaolei
 */
@Data
public class DateEnumBo {


    /**
     * 2022年11月11日
     */
    private String dateChName;

    /**
     * YYYY-MM-DD
     */
    private String dateFormatStr;


    /**
     * {{today}}
     */
    private String dateScript;


    private String dateReplaceScript;


    /**
     * 尾号
     */
    private Integer num;
}
