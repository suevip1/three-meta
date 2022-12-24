package com.coatardbul.baseService.entity.dto;

import lombok.Data;

import java.util.List;

/**
    *
    */
@Data
public class StockCronRefreshDTO {
    /**
     * YYYY-MM-DD
     */
    private String dateStr;

    /**
     * 目前仅限于定时任务使用
     * HH:mm
     */
    private String timeStr;


    private List<String> codeArr;

}