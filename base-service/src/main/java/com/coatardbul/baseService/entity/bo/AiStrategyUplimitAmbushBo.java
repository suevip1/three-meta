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
public class AiStrategyUplimitAmbushBo {




    /**
     * 昨日收盘最大涨幅
     */
    private BigDecimal lastCloseIncreaseMaxRate;
//    /**
//     * 昨日最大涨幅最大值
//     */
//    private BigDecimal lastMaxIncreaseMaxRate;

    /**
     * 当日竞价最大涨幅
     */
    private BigDecimal auctionIncreaseMaxRate;
//    /**
//     * 涨停后的第二日
//     */
//    private BigDecimal nextMaxIncreaseMaxRate;

    /**
     * 买入涨幅，相对涨停前日收盘价的涨幅，为买入点
     */
    private BigDecimal buyIncreaseMinRate;


}
