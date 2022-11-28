package com.coatardbul.river.model.entity;

import java.util.Date;
import lombok.Data;

/**
 * 菜单资源表
 */
@Data
public class AuthCalendar {
    /**
     * 主键
     */
    private String id;

    /**
     * YYYY-MM-DD
     */
    private String date;

    /**
     * 星期
     */
    private Integer week;

    /**
     * 日期属性 1 工作日 3节假日2 休息日
     */
    private Integer dateProp;

    /**
    * 节日名称
    */
    private String holidayName;

    /**
    * 阴历日期中文
    */
    private String lunar;

    /**
    * 状态 1-有效 0-无效
    */
    private Integer status;

    /**
    * 创建日期
    */
    private Date gmtCreate;

    /**
    * 修改日期
    */
    private Date gmtModified;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 修改人
     */
    private String modifier;
}