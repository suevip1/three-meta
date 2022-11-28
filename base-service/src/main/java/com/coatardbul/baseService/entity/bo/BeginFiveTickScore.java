package com.coatardbul.baseService.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/27
 *
 * @author Su Xiaolei
 */
@Data
public class BeginFiveTickScore {


    /**
     * 是否直接返回
     */
    private Boolean flag= false;

    /**
     * 得分
     */
    private BigDecimal score=BigDecimal.ZERO;
}
