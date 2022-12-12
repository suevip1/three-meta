package com.coatardbul.baseService.utils;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/11
 *
 * @author Su Xiaolei
 */
public class BusiDateTimeUtils {


    /**
     * 添加分钟后的数据
     * @param timeStr 11:12:30
     * @param num
     * @return
     */
    public static String getAfterMinuter(String timeStr,Integer num){
        Integer hour =Integer.valueOf(timeStr.substring(0, 2)) ;
        Integer minuter =Integer.valueOf(timeStr.substring(3, 5)) ;
        if(num+minuter>=60){
            hour++;
            minuter=  minuter+num-60;
        }else {
            minuter=  minuter+num;
        }
        return formatNum(hour)+timeStr.substring(2, 3)+formatNum(minuter)+timeStr.substring(5, timeStr.length());
    }
    private static String  formatNum(Integer num){
        if(num<10){
            return "0"+num;
        }else {
            return String.valueOf(num);
        }
    }
}
