package com.coatardbul.baseCommon.constants;

public enum EsTemplateConfigEnum {
    TYPE_DAY("day", "每日数据", 0),
    TYPE_AUCTION("auction", "竞价数据", 0),
    TYPE_MINUTER("minuter", "分钟数据", 0),
    TYPE_DAY_COUNT("dayCount", "日个数", 0),
    TYPE_MINUTER_COUNT("minuterCount", "分钟个数", 0),

    MODE_FIRST("first", "第一页", 0),
    MODE_ALL("all", "全部", 0),
    LEVEL_SMALL("small", "少量数据", 3*1000),
    LEVEL_MIDDLE("middle", "正常量数据", 5*1000),
    LEVEL_BIG("big", "大量数据", 10*1000);
    private String sign;

    private String desc;


    /**
     * 间隔毫秒数
     */
    private Integer timeInterval;

    public Integer getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(Integer timeInterval) {
        this.timeInterval = timeInterval;
    }

    public static  Integer getTimeInterval(String sign){
        EsTemplateConfigEnum[] values = EsTemplateConfigEnum.values();
        for(EsTemplateConfigEnum esTemplateConfigEnum:values){
            if(esTemplateConfigEnum.getSign().equals(sign)){
                return esTemplateConfigEnum.getTimeInterval();
            }
        }
        return EsTemplateConfigEnum.LEVEL_MIDDLE.getTimeInterval();
    }
    EsTemplateConfigEnum(String type, String desc, Integer timeInterval) {
        this.sign = type;
        this.desc = desc;
        this.timeInterval = timeInterval;
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
