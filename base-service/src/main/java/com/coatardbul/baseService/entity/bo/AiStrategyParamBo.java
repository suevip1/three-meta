package com.coatardbul.baseService.entity.bo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/8
 *
 * @author Su Xiaolei
 */
@Data
public class AiStrategyParamBo {


    /**
     * HH：mm
     */
    private String beginStr;

    private String endStr;

    private String lastEndStr;


    /**
     * 每次金额
     */
    private BigDecimal everyAmount;


    /**
     * 总的买入次数
     */
    private Integer buyCodeNum;


    /**
     * 延迟买入分钟
     */
    private Integer delayMinuter;


    /**
     * 最小涨幅
     */
    private Integer minIncrease;

//    /**
//     * 买入的策略标识，涨停买还是前五买
//     */
//    private String buyStrategySign;

}
