package com.coatardbul.baseService.service;

import com.coatardbul.baseService.entity.bo.PreQuartzTradeDetail;
import com.coatardbul.baseService.entity.bo.StockTemplatePredict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/11
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DuGuRedBlackService extends DuGuSwordDelayService {

    @Autowired
    DuGuSwordDelayService duGuSwordDelayService;

    @Autowired
    ChipService chipService;

    /**
     * 通过tick数据来计算准确的买入位置
     *
     * @param map
     * @param result
     * @param code
     */
    @Override
    public void calcProcess(Map map, PreQuartzTradeDetail result, String code) {
        duGuSwordDelayService.calcProcess(map, result, code);
        calcChipProcess(map, result);
    }


    /**
     * 筹码计算
     *
     * @param map
     * @param result
     */
    public void calcChipProcess(Map map, PreQuartzTradeDetail result) {
        StockTemplatePredict stockTemplatePredict = new StockTemplatePredict();
        stockTemplatePredict.setCode(map.get("code").toString());
        stockTemplatePredict.setDate(map.get("dateStr").toString());
        chipService.calcDongCaiSaleInfo(stockTemplatePredict);
        boolean flag = verifyResult(stockTemplatePredict);
        if (!flag) {
            result.setTradeFlag(false);
        }
    }


    private boolean verifyResult(StockTemplatePredict stockTemplatePredict) {

        //是否为红色
        BigDecimal subtract = new BigDecimal(stockTemplatePredict.getLastConcentrationRatio()).subtract(new BigDecimal(stockTemplatePredict.getConcentrationRatio()));

        if (subtract.compareTo(new BigDecimal(0.8)) >= 0 && subtract.compareTo(new BigDecimal(2.2)) <= 0) {
            if (new BigDecimal(stockTemplatePredict.getEarnProfit()).compareTo(new BigDecimal(91)) >= 0) {
                return true;
            }
        }
        if (subtract.compareTo(new BigDecimal(0.3)) >= 0 && subtract.compareTo(new BigDecimal(0.8)) <= 0) {
            if (new BigDecimal(stockTemplatePredict.getEarnProfit()).compareTo(new BigDecimal(75)) >= 0) {
                return true;
            }
        }

        if (subtract.compareTo(new BigDecimal(-0.3)) >= 0 && subtract.compareTo(new BigDecimal(0.25)) <= 0) {
            if (new BigDecimal(stockTemplatePredict.getEarnProfit()).compareTo(new BigDecimal(92)) >= 0) {
                return true;
            }
        }
        if (subtract.compareTo(new BigDecimal(-6)) >= 0 && subtract.compareTo(new BigDecimal(-2.5)) <= 0) {
            if (new BigDecimal(stockTemplatePredict.getEarnProfit()).compareTo(new BigDecimal(90)) >= 0) {
                return true;
            }
        }
        if (subtract.compareTo(new BigDecimal(-1.5)) >= 0 && subtract.compareTo(new BigDecimal(-0.5)) <= 0) {
            if (new BigDecimal(stockTemplatePredict.getEarnProfit()).compareTo(new BigDecimal(97)) >= 0) {
                return true;
            }
        }

        return false;

    }


}
