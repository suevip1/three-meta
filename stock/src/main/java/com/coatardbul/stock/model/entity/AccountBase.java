package com.coatardbul.stock.model.entity;

import java.util.Date;
import lombok.Data;

@Data
public class AccountBase {
    private String id;

    /**
     * 类型：抖音，东财
     */
    private String tradeType;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 过期时间
     */
    private Date expireTime;

    private String cookie;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 参数1：目前用户保存东财有效key
     */
    private String param1;

    /**
     * 参数2，保留字段
     */
    private String param2;

    /**
     * 描述
     */
    private String remark;
}