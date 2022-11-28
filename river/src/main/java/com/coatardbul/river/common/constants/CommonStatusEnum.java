package com.coatardbul.river.common.constants;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2021/12/26
 *
 * @author Su Xiaolei
 */
public enum CommonStatusEnum {
    INVALID(Short.valueOf((short)0), "无效、冻结、禁用"),
    VALID(Short.valueOf((short)1), "有效、启用"),
    DELETED(Short.valueOf((short)9), "删除");

    private Short code;
    private String desc;

    private CommonStatusEnum(Short code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static CommonStatusEnum valueOf(short code) {
        CommonStatusEnum[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            CommonStatusEnum commonStatusEnum = var1[var3];
            if (commonStatusEnum.code == code) {
                return commonStatusEnum;
            }
        }

        throw new IllegalArgumentException(String.format("无效的编码,CommonStatusEnum【%s】", code));
    }

    public Short getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }
}

