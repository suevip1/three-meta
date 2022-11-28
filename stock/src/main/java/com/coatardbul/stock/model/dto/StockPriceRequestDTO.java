package com.coatardbul.stock.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2021/7/8
 *
 * @author Su Xiaolei
 */
@Data
@ApiModel
public class StockPriceRequestDTO {

    @ApiModelProperty(value = "股票code")
    private String code;


    private String beginDate;
    
    private String endDate;
}
