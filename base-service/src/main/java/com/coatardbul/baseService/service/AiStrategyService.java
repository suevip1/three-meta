package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.constants.BuySellQuartzStrategySignEnum;
import com.coatardbul.baseCommon.constants.IsNotEnum;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.AiStrategyParamBo;
import com.coatardbul.baseService.entity.bo.PreQuartzTradeDetail;
import com.coatardbul.baseService.entity.bo.PreTradeDetail;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/8
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class AiStrategyService {
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StockParseAndConvertService stockParseAndConvertService;

    /**
     * 获取预交易的配置信息
     * @param code
     * @return
     */
    public PreQuartzTradeDetail getPreQuartzTradeDetail(String code) {
        PreQuartzTradeDetail result = new PreQuartzTradeDetail();
        String key = RedisKeyUtils.getNowStockInfo(code);
        String stockDetailStr = (String) redisTemplate.opsForValue().get(key);
        Map map = JsonUtil.readToValue(stockDetailStr, Map.class);
        if (map.get("aiStrategySign") == null) {
            return result;
        }
        if (AiStrategyEnum.DU_GU_SWORD.getCode().equals(map.get("aiStrategySign"))) {
            String strategyParam = (String) redisTemplate.opsForValue().get(RedisKeyUtils.getAiStrategyParam(AiStrategyEnum.DU_GU_SWORD.getCode()));
            AiStrategyParamBo aiStrategyParamBo = JsonUtil.readToValue(strategyParam, AiStrategyParamBo.class);
            //判断当前是否已经观察，交易中
            String preTradeCodeKey = RedisKeyUtils.getPreTradeCode();
            if (redisTemplate.hasKey(preTradeCodeKey)) {
                String preTradeCode = (String)redisTemplate.opsForValue().get(preTradeCodeKey);
                Map preTradeCodeMap = JsonUtil.readToValue(preTradeCode, Map.class);
                if (preTradeCodeMap.containsKey(code)) {
                    return result;
                }
            }
            //时间是否符合,当前时间小于结束时间
            if (DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM).compareTo(aiStrategyParamBo.getEndStr()) <= 0
            &&DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM).compareTo(aiStrategyParamBo.getBeginStr()) >= 0) {
                //条件是否复合
                conditionProcess(map,aiStrategyParamBo,result,code);
            }
        }
        return result;
    }


    /**
     * 条件判断
     * @param map
     * @param aiStrategyParamBo
     * @param result
     * @param code
     */
    private void conditionProcess(Map map, AiStrategyParamBo aiStrategyParamBo, PreQuartzTradeDetail result,String code) {
        BigDecimal auctionIncreaseRate = new BigDecimal(map.get("auctionIncreaseRate").toString());
        BigDecimal lastClosePrice = new BigDecimal(map.get("lastClosePrice").toString());
        //最大价格
        BigDecimal maxPrice = new BigDecimal(map.get("maxPrice").toString());
        BigDecimal maxIncreaseRate = maxPrice.subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
        //最大价格涨幅-竞价涨幅=涨幅（预警计算方式）
        BigDecimal subIncreaseRate = maxIncreaseRate.subtract(auctionIncreaseRate);
        //竞价涨幅在 -2 到 0  之间，最高点到达7个点， 9个点买入，或者涨停前五买入
        if (auctionIncreaseRate.compareTo(new BigDecimal(-2)) >= 0 && auctionIncreaseRate.compareTo(BigDecimal.ZERO) <= 0) {
            if (subIncreaseRate.compareTo(new BigDecimal(7)) >= 0) {
                //计算
                calcProcess(map,aiStrategyParamBo,result,code);
            }
        }
        //竞价涨幅在 0 到 2 之间， 6个点预警， 9个点买入，或者涨停前五买入
        if (auctionIncreaseRate.compareTo(BigDecimal.ZERO) >= 0 && auctionIncreaseRate.compareTo(new BigDecimal(3)) <= 0) {
            if (subIncreaseRate.compareTo(new BigDecimal(6)) >= 0) {
                //计算
                calcProcess(map,aiStrategyParamBo,result,code);
            }
        }
        //竞价涨幅在 3 之间， 5个点预警， 9个点买入，或者涨停前五买入
        if (auctionIncreaseRate.compareTo(new BigDecimal(3)) >= 0 && auctionIncreaseRate.compareTo(new BigDecimal(4)) <= 0) {
            if (subIncreaseRate.compareTo(new BigDecimal(5)) >= 0) {
                //计算
                calcProcess(map,aiStrategyParamBo,result,code);
            }
        }

    }

    private void calcProcess(Map map, AiStrategyParamBo aiStrategyParamBo, PreQuartzTradeDetail result,String code) {
        String name = map.get("name").toString();
        result.setTradeFlag(true);
        result.setCode(code);
        result.setName(name);
        BigDecimal lastClosePrice = new BigDecimal(map.get("lastClosePrice").toString());

        BigDecimal upLimitPrice = stockParseAndConvertService.getUpLimit(lastClosePrice);

        BigDecimal upLimitFivePrice = upLimitPrice.subtract(new BigDecimal(0.01));
        //涨幅前五小于9 ,走大于9的策略，否则走涨停前五
        BigDecimal divide = upLimitFivePrice.subtract(lastClosePrice).multiply(new BigDecimal(100)).divide(lastClosePrice, 2, BigDecimal.ROUND_HALF_DOWN);
        if(divide.compareTo(new BigDecimal(9))<=0){
            result.setQuartzSign(BuySellQuartzStrategySignEnum.RATE_GREATE_BUY.getCode());
        }else {
            result.setQuartzSign(BuySellQuartzStrategySignEnum.UPLIMIT_BUY_FIVE.getCode());
        }
        addCodeToPreTrade(code);
        calcSuitPrice(map, aiStrategyParamBo, result);
    }

    /**
     * 计算合适的价格
     * @param map
     * @param aiStrategyParamBo
     * @param result
     */
    private void calcSuitPrice(Map map, AiStrategyParamBo aiStrategyParamBo, PreQuartzTradeDetail result){
        BigDecimal newPrice =new BigDecimal( map.get("newPrice").toString());
        //不够一次
        BigDecimal buyNum = aiStrategyParamBo.getEveryAmount().divide(new BigDecimal(100)).divide(newPrice, 2, BigDecimal.ROUND_HALF_DOWN);
        if(buyNum.compareTo(BigDecimal.ONE)<0){
            result.setTradeNum(new BigDecimal(100));
        }else {
            result.setUserMoney(aiStrategyParamBo.getEveryAmount());
        }

    }


    /**
     * 将code 添加到预交易的key中
     * @param code
     */
    private void addCodeToPreTrade(String code){
        String preTradeCodeKey = RedisKeyUtils.getPreTradeCode();
        Map preTradeCodeMap = null;
        if(redisTemplate.hasKey(preTradeCodeKey)){
             preTradeCodeMap = JsonUtil.readToValue(preTradeCodeKey, Map.class);
        }else {
            preTradeCodeMap=new HashMap();
        }
        preTradeCodeMap.put(code, code);
        redisTemplate.opsForValue().set(preTradeCodeKey, JsonUtil.toJson(preTradeCodeMap), 4, TimeUnit.HOURS);
    }
}
