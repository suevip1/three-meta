package com.coatardbul.stock.model.entity;

import lombok.Data;

/**
 * 日切表
 */
@Data
public class StockTradeDateSwitch {
    /**
     * 日期 YYYY-MM-DD
     */
    private String date;

    /**
     * 时间：HH:mm
     */
    private String time;
}