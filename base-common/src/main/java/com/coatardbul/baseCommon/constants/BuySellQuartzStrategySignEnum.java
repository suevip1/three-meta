package com.coatardbul.baseCommon.constants;

public enum BuySellQuartzStrategySignEnum {


    TIME_BUY("TIME_BUY","定时买入"),
    V_BUY("V_BUY","V买"),
    UPLIMIT_BUY("UPLIMIT_BUY","涨停买入"),
    UPLIMIT_BUY_FIVE("UPLIMIT_BUY_FIVE","涨停前五买入"),
    RATE_GREATE_BUY("RATE_GREATE_BUY","涨幅大于几买入"),
    REBOUND_GREATE_BUY("REBOUND_GREATE_BUY","T买入")

    ;

    private String code;

    private String desc;


    BuySellQuartzStrategySignEnum(String code, String desc) {
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
