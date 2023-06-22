package com.coatardbul.stock.service.statistic.tradeQuartz;

import com.coatardbul.baseCommon.constants.SimulateTypeEnum;
import com.coatardbul.baseService.entity.bo.PreTradeDetail;
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
public class LowRateSellTradeService extends SellTradeService {

    public Boolean tradeProcess(BigDecimal lessRate, BigDecimal minRate, BigDecimal userMoney, BigDecimal buyNum, String code, String userName) {
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

        //真实交易
        if (SimulateTypeEnum.REAL.getSign().equals(commonTradeInfo.getDefaultStockTradeConfig().getSimulateType())) {
            //最大涨幅减去目前涨幅
            BigDecimal subtract = commonTradeInfo.getStockBaseDetail().getMaxUpRate().subtract(commonTradeInfo.getStockBaseDetail().getCurrUpRate());
            if (subtract.compareTo(lessRate) > 0) {
                //最低的线在哪成交，不可能跌停立马出掉，万一回弹呢，即目前价格涨幅大于最低线
                if (minRate.compareTo(commonTradeInfo.getStockBaseDetail().getCurrUpRate()) < 0) {
                    flag = sellTrade(preTradeDetail, userName);
                }
            }
        }


        //后缀
        suffixHandle(commonTradeInfo, preTradeDetail,flag);
        return flag;

    }
}
