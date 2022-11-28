package com.coatardbul.stock.model.dto;

import lombok.Data;

/**
    *
    */
@Data
public class StockEmotionQueryDTO {
    /**
     * YYYY-MM-DD
     */
    private String dateStr;


    /**
     * 对象枚举标识
     */
    private String objectEnumSign;


}