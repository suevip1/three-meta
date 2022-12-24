package com.coatardbul.baseCommon.model.bo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/15
 *
 * @author Su Xiaolei
 */
@Data
public class Chip {


    /**
     * 获利比例
     */
    private BigDecimal benefitPart;

    /**
     * 平均成本
     */
    private BigDecimal avgCost;

    /**
     * 集中度
     */
    private BigDecimal concentration;



    private List<Double> xcoord;


    private List<Double> ycoord;


}
