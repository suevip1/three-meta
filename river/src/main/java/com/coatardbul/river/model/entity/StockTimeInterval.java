package com.coatardbul.river.model.entity;

import lombok.Data;

/**
    * 时间间隔表
    */
@Data
public class StockTimeInterval {
    private String id;

    private String timeStr;

    /**
    * 间隔时间，单位分钟
    */
    private Integer intervalType;
}