package com.coatardbul.baseService.utils;

import com.coatardbul.baseCommon.util.DateTimeUtil;

import java.util.Date;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/21
 *
 * @author Su Xiaolei
 */
public class RedisKeyUtils {



    public static String getNowStockInfo(String code) {
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        String key = getHisStockInfo(dateFormat, code);
        return key;
    }

    public static String getHisStockInfo(String dateFormat, String code) {
        String key = dateFormat + "_stock_" + code;
        return key;
    }

    public static String getStockInfoPattern(String dateFormat) {
        String key = dateFormat + "_stock_*" ;
        return key;
    }
    public static String getCodeByStockInfoKey(String key) {
        return key.substring(key.length()-6,key.length());
    }






    /**
     * 获取redis股票分钟key
     *
     * @param code
     * @return
     */
    public static String getNowStockMinuterInfo(String code) {
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        return getHisStockMinuterInfo(dateFormat,code);
    }

    public static String getHisStockMinuterInfo(String dateFormat, String code) {
        return dateFormat + "_minuter_" + code;
    }




    /**
     * 获取redis股票Tick key
     *
     * @param code
     * @return
     */
    public static String getNowStockTickInfo(String code) {
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        return getHisStockTickInfo(dateFormat, code);
    }

    public static String getHisStockTickInfo(String dateFormat, String code) {
        return dateFormat + "_tick_" + code;
    }


    /**
     * 获取ai 策略
     * @param dateFormat
     * @param code
     * @return
     */
    public static String getAiStrategyParam( String strategySign) {
        return "ai_strategy_" + strategySign;
    }


    public static String getPreTradeCode( ) {
        return "pre_trade_code" ;
    }



    public static String getStockPool(String dateStr){
        return dateStr+"_pool";
    }
}
