package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/7/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockUpLimitAnalyzeService {


    public Map convert(JSONObject jsonObject, String dateStr) {

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("dateStr", dateStr);
        dateStr=dateStr.replaceAll("-","");
        Set<String> keys = jsonObject.keySet();
        for (String key : keys) {
            if (key.equals("封单范围")) {
                jsonMap.put("upLimitVolRange", jsonObject.get(key));
            }
            if (key.equals("首次涨停时间")) {
                jsonMap.put("firstUplimitTime", jsonObject.get(key));
            }
            if (key.equals("首次封单差值")) {
                jsonMap.put("upLimitMixSubVolRange", jsonObject.get(key));
            }
            if (key.equals("有效封单差值")) {
                jsonMap.put("upLimitMaxSubVolRange", jsonObject.get(key));
            }
            if (key.equals("打开涨停次数")) {
                jsonMap.put("upLimitOpenNum", jsonObject.get(key));
            }
            if (key.equals("涨停强弱概览")) {
                jsonMap.put("upLimitDetail", jsonObject.get(key));
            }
            if (key.equals("code")) {
                jsonMap.put("code", jsonObject.get(key));
            }
            if (key.equals("股票简称")) {
                jsonMap.put("name", jsonObject.get(key));
            }
            if (key.indexOf("竞价涨幅") > -1 && key.indexOf(dateStr) > -1
            ) {
                jsonMap.put("auctionIncreaseRate", jsonObject.get(key));
            }
            if (key.indexOf("1日avl") > -1 && key.indexOf("收盘价:不复权") > -1
            ) {
                jsonMap.put("avgPriceRate", jsonObject.get(key));
            }
            //昨日竞价涨幅
            if (key.indexOf("竞价涨幅") > -1 && key.indexOf(dateStr) == -1
            ) {
                jsonMap.put("lastAuctionIncreaseRate", jsonObject.get(key));
            }
            if (key.indexOf("竞价金额") > -1 && key.indexOf(dateStr) > -1 && key.indexOf("{/}") < 0
            ) {
                jsonMap.put("auctionTradeAmount", jsonObject.get(key));
            }
            if (key.indexOf("总市值") > -1 && key.indexOf(dateStr) > -1
            ) {
                jsonMap.put("marketValue", jsonObject.get(key));
            }
            if (key.indexOf("涨停原因类别") > -1
            ) {
                jsonMap.put("theme", jsonObject.get(key));
            }
            if (key.indexOf("市值") > -1 && key.indexOf("总") < 0
            ) {
                jsonMap.put("circulationMarketValue", jsonObject.get(key));
            }
            if (key.indexOf("股票代码") > -1
            ) {
                String[] split = jsonObject.getString(key).split("\\.");
                jsonMap.put("codeUrl", split[1].toLowerCase() + split[0]);
            }
            if (key.indexOf("换手率") > -1 && key.indexOf("分时") < 0 && key.indexOf(dateStr) < 0 && key.indexOf("{/}") < 0
            ) {
                jsonMap.put("lastTurnOverRate", jsonObject.get(key));
            }
            if (key.indexOf("量比") > -1 && key.indexOf(dateStr) < 0 && key.indexOf("{/}") < 0
            ) {
                jsonMap.put("lastVolRate", jsonObject.get(key));
            }
            if (key.indexOf("成交额") > -1 && key.indexOf(dateStr) < 0 && key.indexOf("{/}") < 0 && key.indexOf("分时") < 0
            ) {
                jsonMap.put("lasttradeAmount", jsonObject.get(key));
            }

            if (key.indexOf("涨跌幅") > -1 && key.indexOf(dateStr) > -1
            ) {
                jsonMap.put("newIncreaseRate", jsonObject.get(key));
            }
            if (key.indexOf("成交额") > -1 && key.indexOf(dateStr) > -1 && key.indexOf("{/}") < 0
            ) {
                jsonMap.put("tradeAmount", jsonObject.get(key));
            }
            if (key.indexOf("分时换手率") > -1 && key.indexOf(dateStr) > -1
            ) {
                jsonMap.put("auctionTurnOverRate", jsonObject.get(key));
            }
            if (key.indexOf("分时量比") > -1 && key.indexOf(dateStr) > -1 && key.indexOf("09:25") > 0
            ) {
                jsonMap.put("auctionVol", jsonObject.get(key));
            }
            if (key.indexOf("换手率") > -1 && key.indexOf(dateStr) > -1 && key.indexOf("分时") < 0
            ) {
                jsonMap.put("turnOverRate", jsonObject.get(key));
            }
            if (key.indexOf("收盘价") > -1 && key.indexOf(dateStr) > -1
            ) {
                jsonMap.put("newPrice", jsonObject.get(key));
            }

        }
        BigDecimal subtract =convert(jsonMap.get("newIncreaseRate"))
                .subtract(convert(jsonMap.get("auctionIncreaseRate")));
        jsonMap.put("subIncreaseRate", subtract);
        //昨日换手/昨日量比  是否埋伏
        BigDecimal divide = convert(jsonMap.get("lastTurnOverRate"))
                .divide(convert(jsonMap.get("lastVolRate")), 2, BigDecimal.ROUND_HALF_UP);
        jsonMap.put("compressionDivision", divide);
        //竞价动能比例，今日竞价金额/昨日成交额
        BigDecimal divide1 = convert(jsonMap.get("auctionTradeAmount")).multiply(new BigDecimal(100))
                .divide(convert(jsonMap.get("lasttradeAmount")), 2, BigDecimal.ROUND_HALF_UP);
        jsonMap.put("auctionPowerRate", divide1);
        jsonMap.put("objectSign", StockTemplateEnum.FIRST_UP_LIMIT_WATCH_TWO.getSign());
        BigDecimal bigDecimal = convert(jsonMap.get("lastAuctionIncreaseRate"));
        return jsonMap;
//        if(bigDecimal.compareTo(new BigDecimal(-5))>0&&bigDecimal.compareTo(new BigDecimal(3))<0){
//            return jsonMap;
//        }else {
//            return new HashMap();
//        }
    }

    private BigDecimal convert(Object obj){
        if(obj==null){
            return BigDecimal.ZERO;
        }else {
            return new BigDecimal(String.valueOf(obj));
        }
    }


}
