package com.coatardbul.stock.model.bo.trade;

import com.coatardbul.baseCommon.model.bo.trade.StockBaseDetail;
import com.coatardbul.stock.model.entity.StockTradeConfig;
import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/8/16
 *
 * @author Su Xiaolei
 */
@Data
public class TradeAllConfigDetail {


    /**
     * 账号配置
     */
    private StockTradeConfig defaultStockTradeConfig;


    /**
     * 涨停价
     */
    private StockBaseDetail stockBaseDetail;


    private  String date;

    private String time;
}
