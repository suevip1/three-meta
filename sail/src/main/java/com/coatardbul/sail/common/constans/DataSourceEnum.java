package com.coatardbul.sail.common.constans;

public enum DataSourceEnum {
    XIN_LANG("xinLang","新浪财经"),
    DONG_FANG("dongFang","东方财富");

    private String sign;

    private String desc;

    DataSourceEnum(String sign, String desc) {
        this.sign = sign;
        this.desc = desc;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
