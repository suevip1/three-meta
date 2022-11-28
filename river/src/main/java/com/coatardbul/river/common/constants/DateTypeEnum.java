package com.coatardbul.river.common.constants;

/**
 * @author Su Xiaolei
 */public enum DateTypeEnum {
    /**
     *工作日
     */
    WORK_DAY(1, "工作日"),
    /**
     *周六，周日
     */
    WEEK_DAY(2, "周六，周日"),
    /**
     *节假日
     */
    HOLIDAY(3, "节假日"),

    WORK_WEEK_DAY(4, "周六，周日加班");

    private Integer code;
    private String desc;

    DateTypeEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
