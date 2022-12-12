package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.constants.BuySellQuartzStrategySignEnum;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.AiStrategyParamBo;
import com.coatardbul.baseService.entity.bo.PreQuartzTradeDetail;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.utils.BusiDateTimeUtils;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
public class DuGuSwordDelayService extends DuGuSwordCommonService {


    /**
     * 通过tick数据来计算准确的买入位置
     *
     * @param map
     * @param result
     * @param code
     */
    @Override
    public void calcProcess(Map map, PreQuartzTradeDetail result, String code) {
        String name = map.get("name").toString();
        result.setCode(code);
        result.setName(name);
        BigDecimal lastClosePrice = new BigDecimal(map.get("lastClosePrice").toString());

        BigDecimal upLimitPrice = stockParseAndConvertService.getUpLimit(lastClosePrice);

        BigDecimal upLimitFivePrice = upLimitPrice.subtract(new BigDecimal(0.01));
        //涨幅前五小于9 ,走大于9的策略，否则走涨停前五
        result.setQuartzSign(BuySellQuartzStrategySignEnum.RATE_GREATE_BUY.getCode());


        calcSuitPrice(map, result, upLimitFivePrice, upLimitPrice);
    }


    /**
     * 计算合适的价格，通过tick
     *
     * @param map
     * @param result
     */
    @Override
    public void calcSuitPrice(Map map, PreQuartzTradeDetail result, BigDecimal upLimitFivePrice, BigDecimal upLimitPrice) {
        String currDateStr = map.get("currDateStr").toString();
        String code = map.get("code").toString();
        BigDecimal lastClosePrice = new BigDecimal(map.get("lastClosePrice").toString());

        String strategyParam = (String) redisTemplate.opsForValue().get(RedisKeyUtils.getAiStrategyParam(AiStrategyEnum.DU_GU_SWORD_DELAY.getCode()));
        AiStrategyParamBo aiStrategyParamBo = JsonUtil.readToValue(strategyParam, AiStrategyParamBo.class);

        BigDecimal target = null;
        //大于9，可以买
        if (BuySellQuartzStrategySignEnum.RATE_GREATE_BUY.getCode().equals(result.getQuartzSign())) {
            target = lastClosePrice.multiply(new BigDecimal(1.09));
        } else {
            target = upLimitFivePrice;
        }
        BigDecimal greateNinePrice = lastClosePrice.multiply(new BigDecimal(1.09));
        String key = RedisKeyUtils.getHisStockTickInfo(currDateStr, code);
        String stockTickArrStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(stockTickArrStr)) {
            List<TickInfo> stockTickArr = JsonUtil.readToValue(stockTickArrStr, new TypeReference<List<TickInfo>>() {
            });
            BigDecimal finalTarget = target;
            //预买入，价格大于涨停前五，时间开盘后可以买入的时间
            List<TickInfo> collect = stockTickArr.stream().filter(item -> item.getTime().compareTo("09:29:55") > 0 && item.getPrice().compareTo(finalTarget) >= 0).collect(Collectors.toList());
            if (collect.size() > 0) {
                //预买入时间点
                TickInfo tickInfo = collect.get(0);
                String buyMinuter = BusiDateTimeUtils.getAfterMinuter(tickInfo.getTime(), aiStrategyParamBo.getDelayMinuter());
                //准确买入时间
                String endDateStr = aiStrategyParamBo.getLastEndStr() + ":00";
                List<TickInfo> buyTickInfo = stockTickArr.stream().filter(item -> item.getTime().compareTo(buyMinuter) >= 0
                        && item.getTime().compareTo(endDateStr) <= 0
                        && item.getPrice().compareTo(greateNinePrice) >= 0).collect(Collectors.toList());
                if (buyTickInfo.size() > 0) {
                    TickInfo downMinTickInfo = buyTickInfo.stream().min(Comparator.comparing(TickInfo::getPrice)).get();
                    if (downMinTickInfo.getPrice().compareTo(upLimitPrice) < 0) {
                        result.setTradeFlag(true);
                        result.setDate(currDateStr);
                        result.setTime(buyTickInfo.get(0).getTime());
                        result.setPrice(buyTickInfo.get(0).getPrice());
                        //计算涨幅
                        BigDecimal currIncreaseRate = buyTickInfo.get(0).getPrice().subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
                        BigDecimal closeIncreaseRate = stockTickArr.get(stockTickArr.size()-1).getPrice().subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
                        result.setIncreaseRate(currIncreaseRate);
                        result.setCloseIncreaseRate(closeIncreaseRate);
                    }
                }
            }
        }
    }

}
