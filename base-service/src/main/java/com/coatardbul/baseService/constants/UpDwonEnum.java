package com.coatardbul.baseService.constants;

public enum UpDwonEnum {
    UP("UP","涨"),
    DOWN("DOWN","跌"),
    EQUAL("EQUAL","平");

    private String type;

    private String desc;

    UpDwonEnum(String type, String desc) {
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
}
