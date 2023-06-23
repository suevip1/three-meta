package com.coatardbul.baseCommon.constants;

public enum CookieTypeEnum {

    DONG_FANG_CAI_FU_TRADE("dong_fang_cai_fu_trade", "东方财富交易"),
    DONG_FANG_CAI_FU_NORMAL("dong_fang_cai_fu_normal", "东方财富普通账号"),
    TONG_HUA_SHUN("tong_hua_shun", "同花顺"),

    DOU_YIN("dou_yin", "抖音");


    private String type;

    private String desc;


    CookieTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }


    public static String getDescByCode(String code) {
        CookieTypeEnum[] values = CookieTypeEnum.values();
        for (CookieTypeEnum aiStrategyEnum : values) {
            if (aiStrategyEnum.getType().equals(code)) {
                return aiStrategyEnum.getDesc();
            }
        }
        return "";
    }
}
