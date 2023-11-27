package com.coatardbul.stock.model.dto;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/27
 *
 * @author Su Xiaolei
 */
@Data
public class EsDayKlineDto {

    private String beginDateStr;

    private String endDateStr;


    private String code;
}
