package com.coatardbul.stock.model.dto;

import lombok.Data;

import java.util.List;

/**
    *
    */
@Data
public class DongFangPlateDTO {
    /**
     * YYYY-MM-DD
     */
    private String dateStr;

    /**
     * 目前仅限于定时任务使用
     * HH:mm
     */
    private String timeStr;


    private String gid;

    private List<String> codeArr;

}