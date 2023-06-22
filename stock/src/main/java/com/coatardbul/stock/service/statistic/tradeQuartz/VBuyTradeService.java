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
public class VBuyTradeService extends BuyTradeService {

    public Boolean tradeProcess(BigDecimal userMoney, BigDecimal buyNum, String code, String userName) {
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
        //买入卖出分类计算

        //真实交易
        if (SimulateTypeEnum.REAL.getSign().equals(commonTradeInfo.getDefaultStockTradeConfig().getSimulateType())) {
            flag = buyTrade(preTradeDetail, userName);
        }

        //后缀
        suffixHandle(commonTradeInfo, preTradeDetail, flag);
        return flag;

    }
}
