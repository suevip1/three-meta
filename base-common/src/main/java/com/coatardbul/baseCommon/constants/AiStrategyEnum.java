package com.coatardbul.baseCommon.constants;

public enum AiStrategyEnum {


    DU_GU_SWORD("DU_GU_SWORD","独孤一剑");

    private String code;

    private String desc;


    AiStrategyEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
