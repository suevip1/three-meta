package com.coatardbul.baseService.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/23
 *
 * @author Su Xiaolei
 */
@Data
public class TickInfo {

    private String time;

    private BigDecimal vol;

    private BigDecimal price;

    /**
     * EQUAL,DOWN,UP
     */
    private String buySellFlag;



}
