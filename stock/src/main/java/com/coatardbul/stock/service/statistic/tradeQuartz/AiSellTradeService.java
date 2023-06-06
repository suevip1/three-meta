package com.coatardbul.stock.service.statistic.tradeQuartz;

import com.coatardbul.baseCommon.constants.SimulateTypeEnum;
import com.coatardbul.baseService.entity.bo.PreTradeDetail;
import com.coatardbul.stock.model.bo.trade.StockBaseDetail;
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
public class AiSellTradeService extends SellTradeService {

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


        //真实交易
        if (SimulateTypeEnum.REAL.getSign().equals(commonTradeInfo.getDefaultStockTradeConfig().getSimulateType())) {
            //涨幅
            if (increaseSubtractAiTrade(commonTradeInfo.getStockBaseDetail())) {
                flag = sellTrade(preTradeDetail);
            }
            if (increaseAiTrade(commonTradeInfo.getStockBaseDetail())) {
                flag = sellTrade(preTradeDetail);
            }
        }


        //后缀
        suffixHandle(commonTradeInfo, preTradeDetail, flag);
        return flag;

    }

    private Boolean increaseAiTrade(StockBaseDetail stockBaseDetail) {
        if (stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.09)) > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 智能ai交易，最大涨幅和目前涨幅，早晨9.40到10.30
     * <p>
     * 1-2 最多1个点
     * 2-5最多2个点
     * 4-9,最多2.5个点
     * 9-10 最多3个点
     * 9个点卖出
     */
    private Boolean increaseSubtractAiTrade(StockBaseDetail stockBaseDetail) {
        if (stockBaseDetail.getMaxUpRate().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal subtract = stockBaseDetail.getMaxUpRate().subtract(stockBaseDetail.getCurrUpRate());

            if (stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.01)) > 0 && stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.02)) <= 0) {
                if (subtract.compareTo(new BigDecimal(0.01)) > 0) {
                    return true;
                }
            }
            if (stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.02)) > 0 && stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.05)) <= 0) {
                if (subtract.compareTo(new BigDecimal(0.02)) > 0) {
                    return true;
                }
            }
            if (stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.04)) > 0 && stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.09)) <= 0) {
                if (subtract.compareTo(new BigDecimal(0.025)) > 0) {
                    return true;
                }
            }
            if (stockBaseDetail.getMaxUpRate().compareTo(new BigDecimal(0.09)) > 0) {
                if (subtract.compareTo(new BigDecimal(0.03)) > 0) {
                    return true;
                }
            }
        }

        return false;
    }
}
