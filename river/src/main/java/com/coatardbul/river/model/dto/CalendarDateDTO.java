package com.coatardbul.river.model.dto;

import lombok.Data;

/**
 * @author  Su Xiaolei
 */@Data
public class CalendarDateDTO {
    /**
     * yyyy-mm-dd
     */
    private String beginDate;

    private String endDate;

    /**
     * 日期属性 1 工作日 3节假日2 休息日
     */
    private Integer dateProp;

    /**
     * 数字为正，为后几天的数据，
     * 数字为负，为前几天的数据
     */
    private Integer addDay;
}
