package com.coatardbul.stock.service.statistic.tradeQuartz;

import com.coatardbul.baseCommon.constants.SimulateTypeEnum;
import com.coatardbul.stock.model.bo.trade.PreTradeDetail;
import com.coatardbul.stock.model.bo.trade.TradeAllConfigDetail;
import lombok.extern.slf4j.Slf4j;
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
public class UpLimitTfiveBuyTradeService extends BuyTradeService {

    public Boolean tradeProcess(BigDecimal userMoney, BigDecimal buyNum, String code) {
        Boolean flag = false;
        TradeAllConfigDetail commonTradeInfo = null;
        try {
            commonTradeInfo = getCommonTradeInfo(code);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return flag;
        }
        //预交易
        PreTradeDetail preTradeDetail = computedTradeInfo(userMoney, buyNum, commonTradeInfo);


        //真实交易,目前价小于涨停价五档盘口
        if (SimulateTypeEnum.REAL.getSign().equals(commonTradeInfo.getDefaultStockTradeConfig().getSimulateType())) {
            //目前价格小于前五
            if (commonTradeInfo.getStockBaseDetail().getCurrPrice().compareTo(
                    commonTradeInfo.getStockBaseDetail().getUpLimitPrice().subtract(new BigDecimal(0.06))
            ) > 0   &&
                    //最小价格不能太高
                    commonTradeInfo.getStockBaseDetail().getMinPrice().compareTo(
                            commonTradeInfo.getStockBaseDetail().getUpLimitPrice().subtract(new BigDecimal(0.1))
                    ) < 0
                    &&
                    (commonTradeInfo.getStockBaseDetail().getLastClosePrice().multiply(new BigDecimal(0.08))).compareTo(
                            commonTradeInfo.getStockBaseDetail().getMinPrice()
                    ) > 0
            ) {
                //调用购买接口，
                flag = buyTrade(preTradeDetail);
            }
        }


        //后缀
        suffixHandle(commonTradeInfo, preTradeDetail, flag);
        return flag;

    }
}
