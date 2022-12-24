package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.model.bo.Chip;
import com.coatardbul.baseCommon.model.bo.ChipPosition;
import com.coatardbul.baseCommon.util.DongCaiUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.StockTemplatePredict;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.Invocable;
import java.math.BigDecimal;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/17
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class ChipService {
    @Autowired
    DongFangCommonService dongFangCommonService;
    @Autowired
    RiverRemoteService riverRemoteService;

    public void calcDongCaiSaleInfo(StockTemplatePredict stockTemplatePredict) {
        String response=null;
        int retryNum = 10;
        while (retryNum>0){
            response = dongFangCommonService.getDayKlineChip(stockTemplatePredict.getCode());
            if(StringUtils.isNotBlank(response)){
                break;
            }else {
                retryNum--;
            }
        }
        if(!StringUtils.isNotBlank(response)){
            return;
        }
        ChipPosition chipPosition = dongFangCommonService.rebuildDayKlineChip(response);
        List<List<String>> dayKlineList = chipPosition.getDayKlineList();
        String toJson = JsonUtil.toJson(dayKlineList);

        Invocable chipInvocable=null;
        try {
            chipInvocable = DongCaiUtil.getChipInvocable();
        } catch (Exception e) {
            log.error("获取东财筹码js异常"+e.getMessage());
            return ;
        }
        //90集中度的和
        BigDecimal sum=BigDecimal.ZERO;
        int num =5;
        for(int i=1;i<=num;i++){
            String lastSpecialDay = riverRemoteService.getSpecialDay(stockTemplatePredict.getDate(), 0-i);
            Integer position = chipPosition.getDatePositionMap().get(lastSpecialDay);

            try {
                Object calcChip = chipInvocable.invokeFunction("calcChip", position, 150, 120, toJson);
                Chip convert = DongCaiUtil.convert(calcChip);
                sum= sum.add(convert.getConcentration());
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        stockTemplatePredict.setLastConcentrationRatio(sum.divide(new BigDecimal(num),2, BigDecimal.ROUND_HALF_DOWN).toString());
        try {
            Integer position = chipPosition.getDatePositionMap().get(stockTemplatePredict.getDate());

            Object calcChip = chipInvocable.invokeFunction("calcChip", position, 150, 120, toJson);
            Chip convert = DongCaiUtil.convert(calcChip);
            stockTemplatePredict.setConcentrationRatio(convert.getConcentration().toString());
            stockTemplatePredict.setEarnProfit(convert.getBenefitPart().toString());
            stockTemplatePredict.setJettonCost(convert.getAvgCost());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

}
