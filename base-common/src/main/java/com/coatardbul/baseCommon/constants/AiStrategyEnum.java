package com.coatardbul.baseCommon.constants;

public enum AiStrategyEnum {

    DU_GU_SWORD_DELAY("DU_GU_SWORD_DELAY","独孤一剑延迟"),
    RED_BLACK_CROSS("RED_BLACK_CROSS","红黑交叉"),
    FINE_OPEN("FINE_OPEN","灵性封板"),

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


    public static String getDescByCode(String code) {
        AiStrategyEnum[] values = AiStrategyEnum.values();
        for(AiStrategyEnum aiStrategyEnum:values){
            if(aiStrategyEnum.getCode().equals(code)){
                return aiStrategyEnum.getDesc();
            }
        }
        return "";
    }
}
