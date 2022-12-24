package com.coatardbul.stock.model.dto;

import com.coatardbul.baseService.entity.dto.StockCronRefreshDTO;
import lombok.Data;

/**
    *
    */
@Data
public class StockCronStrategyTabDTO extends StockCronRefreshDTO {



    private String  strategySign;

}