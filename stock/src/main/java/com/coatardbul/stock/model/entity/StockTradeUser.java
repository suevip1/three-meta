package com.coatardbul.stock.model.entity;

import lombok.Data;

import java.util.Date;

@Data
public class StockTradeUser {
    private String id;

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
}