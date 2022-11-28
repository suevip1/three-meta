package com.coatardbul.river.model.dto;

import lombok.Data;

/**
    * 股票问句模板
    */
@Data
public class StockTemplateListQueryDTO {
    private String id;
    /**
    * 模板名称
    */
    private String name;

    private String exampleStr;
}