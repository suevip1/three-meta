package com.coatardbul.baseCommon.constants;

public enum DictTypeEnum {
    ENCRYPT("encrypt","加密方式"),



    AAAAA("encrypt","加密方式"),;

    private String sign;

    private String desc;

    DictTypeEnum(String sign, String desc) {
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
