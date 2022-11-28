package com.coatardbul.baseCommon.constants;

public enum TradeTypeEnum {
    BUY("B","买入",1),
    SELL("S","卖出",2);

    private String sign;

    private String desc;

    private Integer type;


    TradeTypeEnum(String sign, String desc, Integer type) {
        this.sign = sign;
        this.desc = desc;
        this.type = type;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
