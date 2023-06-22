package com.coatardbul.stock.service.statistic.tradeQuartz;

import com.coatardbul.baseCommon.constants.TradeTypeEnum;
import com.coatardbul.baseService.entity.bo.PreTradeDetail;
import com.coatardbul.stock.model.bo.trade.StockTradeBO;
import com.coatardbul.stock.model.bo.trade.TradeAllConfigDetail;
import com.coatardbul.stock.service.statistic.trade.StockTradeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/8/18
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class SellTradeService extends TradeBaseService{
    @Autowired
    StockTradeService stockTradeService;


    public Boolean sellTrade(PreTradeDetail preTradeDetail, String userName) {
        Boolean flag = false;
        //调用购买接口，
        StockTradeBO stockTradeBO = new StockTradeBO();
        stockTradeBO.setStockCode(preTradeDetail.getCode());
        stockTradeBO.setPrice(preTradeDetail.getPrice().toString());
        stockTradeBO.setAmount(preTradeDetail.getTradeNum().toString());
        stockTradeBO.setZqmc(preTradeDetail.getName());
        String response = stockTradeService.sell(stockTradeBO,userName);
        //成功
        if (StringUtils.isNotBlank(response)) {
            flag = true;
        }
        return flag;
    }


    @Override
    public PreTradeDetail computedTradeInfo(BigDecimal userMoney, BigDecimal buyNum, TradeAllConfigDetail commonTradeInfo) {
        PreTradeDetail result = super.computedTradeInfo(userMoney, buyNum, commonTradeInfo);
        result.setUserMoney(userMoney);
        if (userMoney != null) {
            buyNum = userMoney.divide(commonTradeInfo.getStockBaseDetail().getSugSellPrice().multiply(new BigDecimal(100)), 0, BigDecimal.ROUND_DOWN).multiply(new BigDecimal(100));
        }
        result.setTradeNum(buyNum);
        result.setPrice(commonTradeInfo.getStockBaseDetail().getSugSellPrice());
        result.setTradeType(TradeTypeEnum.SELL.getSign());
        return result;
    }
}
