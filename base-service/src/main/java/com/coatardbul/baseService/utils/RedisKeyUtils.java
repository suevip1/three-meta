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


    /**
     * 获取redis股票分钟key
     * @param code
     * @return
     */
    public static String getNowStockMinuterInfo(String code){
        return  "minuter_" + code;
    }


    /**
     * 获取redis股票Tick key
     * @param code
     * @return
     */
    public static String getNowStockTickInfo(String code){
        return  "tick_" + code;
    }


    public static String getNowStockInfo(String code){
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        String key = dateFormat + "_" + code;
        return key;
    }

}
