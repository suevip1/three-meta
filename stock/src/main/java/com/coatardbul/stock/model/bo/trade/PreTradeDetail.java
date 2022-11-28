package com.coatardbul.stock.model.bo.trade;

import com.coatardbul.stock.common.constants.TradeTypeEnum;
import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>
 * Note: 预交易详情
 * <p>
 * Date: 2022/8/18
 *
 * @author Su Xiaolei
 */
@Data
public class PreTradeDetail {



    private String code;


    private String name;


    private  String date;

    private String time;

    private BigDecimal userMoney;

    private BigDecimal tradeNum;

    private BigDecimal price;

    /**
     * B  S
     * @see TradeTypeEnum
     */
    private String tradeType;

}
