package com.coatardbul.baseCommon.constants;

public enum AiStrategyEnum {

    DU_GU_SWORD_DELAY("DU_GU_SWORD_DELAY", "独孤一剑延迟"),
    RED_BLACK_CROSS("RED_BLACK_CROSS", "红黑交叉"),
    FINE_OPEN("FINE_OPEN", "灵性封板"),
    UPLIMIT_AMBUSH("UPLIMIT_AMBUSH", "涨停伏击"),


//    UPLIMIT_AMBUSH_BUY_ONE("UPLIMIT_AMBUSH_BUY_ONE","涨停伏击买一"),
//    HAVE_UPLIMIT_AMBUSH_BUY_ONE("HAVE_UPLIMIT_AMBUSH_BUY_ONE","涨停伏击买一"),

    TWO_ABOVE_UPLIMIT_AMBUSH("TWO_ABOVE_UPLIMIT_AMBUSH", "两板以上涨停伏击"),

    HAVE_UPLIMIT_AMBUSH("HAVE_UPLIMIT_AMBUSH", "昨曾涨停伏击"),
    AMBUSH_CALLAUCTION_ROB("AMBUSH_CALLAUCTION_ROB", "埋伏竞价抢筹"),

    DU_GU_SWORD("DU_GU_SWORD", "独孤一剑");


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


    public static String getDescByCode(String code) {
        AiStrategyEnum[] values = AiStrategyEnum.values();
        for (AiStrategyEnum aiStrategyEnum : values) {
            if (aiStrategyEnum.getCode().equals(code)) {
                return aiStrategyEnum.getDesc();
            }
        }
        return "";
    }
}
