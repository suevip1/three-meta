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
public class ReboundGreateBuyTradeService extends BuyTradeService {

    public Boolean tradeProcess(BigDecimal  reboundRate ,BigDecimal userMoney, BigDecimal buyNum, String code) {
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
            //反弹比例
            BigDecimal currnReboundRate = commonTradeInfo.getStockBaseDetail().getCurrUpRate().subtract(commonTradeInfo.getStockBaseDetail().getMinUpRate());
            //反弹比例大于预设值
            if (currnReboundRate.compareTo(reboundRate) > 0) {
                //调用购买接口，
                flag = buyTrade(preTradeDetail);
            }
        }

        //后缀
        suffixHandle(commonTradeInfo, preTradeDetail, flag);
        return flag;

    }
}
