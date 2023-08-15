package com.coatardbul.baseCommon.constants;

public enum TradeSignEnum {
    ASSET_POSITION("asset_position","持仓"),

    HIS_DEAL_DATA("his_deal_data","历史交易数据"),
    DEAL_DATA("deal_data","当日交易数据"),
    LOGIN("login","登陆"),

    LOGIN_VALIDATEKEY("login_validatekey","登陆key"),
    BUY_SELL("buy_sell","买卖");

    private String sign;

    private String desc;

    TradeSignEnum(String sign, String desc) {
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
