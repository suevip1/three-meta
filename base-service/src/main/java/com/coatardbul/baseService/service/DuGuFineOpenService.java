package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.AiStrategyParamBo;
import com.coatardbul.baseService.entity.bo.PreQuartzTradeDetail;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.utils.BusiDateTimeUtils;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DuGuFineOpenService extends DuGuSwordDelayService {

    @Autowired
    DuGuSwordDelayService duGuSwordDelayService;

    @Autowired
    ChipService chipService;


    @Override
    public void calcSuitPrice(Map map, PreQuartzTradeDetail result, BigDecimal firstPreBuyPrice, BigDecimal upLimitPrice) {
        String currDateStr = map.get("currDateStr").toString();
        String code = map.get("code").toString();
        BigDecimal lastClosePrice = new BigDecimal(map.get("lastClosePrice").toString());
        //ai策略
        String strategyParam = (String) redisTemplate.opsForValue().get(RedisKeyUtils.getAiStrategyParam(AiStrategyEnum.FINE_OPEN.getCode()));
        AiStrategyParamBo aiStrategyParamBo = JsonUtil.readToValue(strategyParam, AiStrategyParamBo.class);
        //预买入结束时间
        String endDateStr = aiStrategyParamBo.getEndStr() + ":00";
        //最后允许的买入时间，一般是早上
        String lastEndDateStr = aiStrategyParamBo.getLastEndStr() + ":00";
        //大于9，可以买
        BigDecimal target = lastClosePrice.multiply(new BigDecimal(1.09));
        BigDecimal finalBuyTarget = target;
        //历史tick 数据
        String key = RedisKeyUtils.getHisStockTickInfo(currDateStr, code);
        String stockTickArrStr = (String) redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(stockTickArrStr)) {
            List<TickInfo> stockTickArr = JsonUtil.readToValue(stockTickArrStr, new TypeReference<List<TickInfo>>() {
            });
            //预买入，价格==涨停,计算第一次涨停时间
            List<TickInfo> firstPreBuyDelayList = stockTickArr.stream().filter(item -> item.getTime().compareTo("09:29:55") > 0
                    && item.getTime().compareTo(endDateStr) <= 0
                    && item.getPrice().compareTo(upLimitPrice) == 0
            ).collect(Collectors.toList());
            if (firstPreBuyDelayList == null || firstPreBuyDelayList.size() == 0) {
                return;
            }
            //预买入时间点
            TickInfo firstPreBuyTickInfo = firstPreBuyDelayList.get(0);
            //延迟买入时间
            String delayBuyTime = BusiDateTimeUtils.getAfterMinuter(firstPreBuyTickInfo.getTime(), aiStrategyParamBo.getDelayMinuter());

            //从第一次预买入到延迟时间，这段时间是否涨停，判断开板次数
            List<TickInfo> preBuyUpLimitInfo = stockTickArr.stream().filter(item -> item.getTime().compareTo(firstPreBuyTickInfo.getTime()) >= 0
                    && item.getTime().compareTo(delayBuyTime) <= 0
                    && item.getPrice().compareTo(upLimitPrice) == 0).collect(Collectors.toList());
            //todo 计算不太准，因为并不是每分钟都有30个tick数据，不活跃的票没有这么多 预设目标数量
            int targetNum = aiStrategyParamBo.getDelayMinuter() * 20 * 3 / 4;
            //区间烂板
            if (preBuyUpLimitInfo.size() > 5 && preBuyUpLimitInfo.size() < targetNum) {
                return;
            }
            //为好板
            else if (preBuyUpLimitInfo.size() > targetNum) {
                //找到第一次，从买点到开板的时间
                List<TickInfo> buyTickInfo = stockTickArr.stream().filter(item -> item.getTime().compareTo(delayBuyTime) >= 0
                        && item.getTime().compareTo(lastEndDateStr) <= 0
                        && item.getPrice().compareTo(finalBuyTarget) >= 0).collect(Collectors.toList());
                if (buyTickInfo.size() > 0) {
                    TickInfo downMinTickInfo = buyTickInfo.stream().min(Comparator.comparing(TickInfo::getPrice)).get();
                    //最小价格小于涨停价，不然买不上
                    if (downMinTickInfo.getPrice().compareTo(upLimitPrice) < 0) {
                        List<TickInfo> confirmTickInfoList = buyTickInfo.stream().filter(item -> item.getPrice().compareTo(upLimitPrice) < 0).collect(Collectors.toList());
                        //开板时间，开板后一分钟再买
                        String openTime = confirmTickInfoList.get(0).getTime();
                        String openDelayMinuterTime = BusiDateTimeUtils.getAfterMinuter(openTime, 1);
                        //开板一分钟内封着，视为假封，重新定义开板时间（是否真假，需要人工判断）
                        int retryNum = 5;
                        while (retryNum > 0) {
                            String finalOpenTime = openTime;
                            String finalOpenDelayMinuterTime = openDelayMinuterTime;
                            List<TickInfo> oneMinuterUpLimitTickInfo = stockTickArr.stream().filter(item -> item.getTime().compareTo(finalOpenTime) > 0
                                    && item.getTime().compareTo(finalOpenDelayMinuterTime) < 0
                                    && item.getPrice().compareTo(upLimitPrice) == 0).collect(Collectors.toList());
                            //一分钟内有涨停
                            if (oneMinuterUpLimitTickInfo.size() > 0) {
                                openTime = oneMinuterUpLimitTickInfo.get(oneMinuterUpLimitTickInfo.size() - 1).getTime();
                                openDelayMinuterTime = BusiDateTimeUtils.getAfterMinuter(openTime, 1);
                                retryNum--;
                            } else {
                                break;
                            }
                        }
                        if (retryNum == 0) {
                            return;
                        }
                        String finalOpenDelayMinuterTime1 = openDelayMinuterTime;
                        //万手单
                        List<TickInfo> volVerifyTickInfoList = stockTickArr.stream().filter(item -> item.getTime().compareTo("09:29:55") > 0
                                && item.getTime().compareTo(finalOpenDelayMinuterTime1) < 0
                                && item.getPrice().compareTo(upLimitPrice) == 0).collect(Collectors.toList());
                        if (volVerifyTickInfoList.size() > 0) {
                            TickInfo tickInfo = volVerifyTickInfoList.stream().max(Comparator.comparing(TickInfo::getVol)).get();
                            if (tickInfo.getVol().compareTo(new BigDecimal(aiStrategyParamBo.getMinIntervalVol())) <= 0) {
                                return;
                            }
                        }

                        List<TickInfo> reConfirmTickInfo = stockTickArr.stream().filter(item -> item.getTime().compareTo(finalOpenDelayMinuterTime1) >= 0
                                && item.getTime().compareTo(lastEndDateStr) <= 0
                                && item.getPrice().compareTo(finalBuyTarget) >= 0).collect(Collectors.toList());
                        if (reConfirmTickInfo.size() > 0) {
                            //二封和首次买入时间不能过大
                            Integer integer = subSecondTimeStr(openTime, reConfirmTickInfo.get(0).getTime()) / 60;
                            if (integer > aiStrategyParamBo.getMaxOpenMinuter()) {
                                return;
                            }
                            result.setTradeFlag(true);
                            result.setDate(currDateStr);
                            result.setTime(reConfirmTickInfo.get(0).getTime());
                            result.setPrice(reConfirmTickInfo.get(0).getPrice());
                            //计算涨幅
                            BigDecimal currIncreaseRate = reConfirmTickInfo.get(0).getPrice().subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
                            BigDecimal closeIncreaseRate = stockTickArr.get(stockTickArr.size() - 1).getPrice().subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
                            result.setIncreaseRate(currIncreaseRate);
                            result.setCloseIncreaseRate(closeIncreaseRate);
                        }
                    }
                }
            }

        }
    }


}
