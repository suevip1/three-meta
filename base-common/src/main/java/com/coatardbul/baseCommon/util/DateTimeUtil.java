package com.coatardbul.baseCommon.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author: suxiaolei
 * @date: 2019/7/1
 */
public class DateTimeUtil {
    /**
     * 月份补位最大值
     */
    public static final int MONTH_COVER_MAX = 10;
    public static final String MONTH_BEGIN_STRING = "0";
    public static final int MAX_MONTH = 12;
    public static final int MAX_MINUTE = 59;

    public static final int MINUTE = 60;

    public static final String YYYY = "yyyy";


    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM_DD_WORD = "yyyy年MM月dd日";


    public static final String HH_MM_SS = "HH:mm:ss";

    public static final String HH_MM = "HH:mm";


    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取时间字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String getDateFormat(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }


    /**
     * 将字符传解析成Date
     * @param dateStr
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date parseDateStr(String dateStr, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
//        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.parse(dateStr);
    }

    /**
     *将特定的字符串横向转换
     * @param dateStr  输入字符串
     * @param formatSource  输入字符串匹配样式
     * @param formatTarget  目标样式
     * @return
     * @throws ParseException
     */
    public static String parseDateStr(String dateStr, String formatSource,String formatTarget) throws ParseException {
        Date date = parseDateStr(dateStr, formatSource);
        return getDateFormat(date,formatTarget);
    }

    /**
     * 获取前后的日期
     * @param num  可以为正负，正往后，负往前
     * @param field  Calendar.MINUTE
     * @return
     */
    public static Date getBeforeAfterDate(int num,int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(field, num);
        return calendar.getTime();
    }


    public static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return  calendar;
    }



    /**
     * 获取从开始至结束的月分
     *
     * @param beginDate yyyy-MM
     * @param endDate   yyyy-MM
     * @return 获取从开始至结束的月分
     */
    public static List<String> getRangeMonth(String beginDate, String endDate) {
        List<String> result = new ArrayList<>();
        Integer beginYear = Integer.valueOf(beginDate.substring(0, 4));
        Integer beginMonth = getMonth(beginDate);

        int endYear = Integer.parseInt(endDate.substring(0, 4));
        int endMonth = getMonth(endDate);
        //年
        for (int i = beginYear; i <= endYear; i++) {
            if (beginYear.equals(endYear)) {
                for (int j = beginMonth; j <= endMonth; j++) {
                    result.add(getDate(i, j));
                }

            } else {
                if (i == beginYear) {
                    for (int j = beginMonth; j <= MAX_MONTH; j++) {
                        result.add(getDate(i, j));

                    }
                } else if (i == endYear) {
                    for (int j = 1; j <= endMonth; j++) {
                        result.add(getDate(i, j));

                    }
                } else {
                    for (int j = 1; j <= MAX_MONTH; j++) {
                        result.add(getDate(i, j));
                    }
                }
            }

        }
        return result;
    }



    /**
     * 获取从开始至结束的时分
     *
     * @param beginTime HH:mm
     * @param endTime  HH:mm
     * @return 获取从开始至结束的时分
     */
    public static List<String> getRangeMinute(String beginTime, String endTime,Integer interval) {
        List<String> result = new ArrayList<>();
        Integer beginHour = Integer.valueOf(beginTime.substring(0, 2));
        Integer beginMinute = Integer.valueOf(endTime.substring(3, 5));

        Integer endHour = Integer.valueOf(endTime.substring(0, 2));
        Integer endMinute = Integer.valueOf(endTime.substring(3, 5));


        int minuteAdd=0;

        for (int i = beginHour; i <= endHour; i++) {
            //小时相等
            if (beginHour.equals(endHour)) {
                for (int j = beginMinute; j <= endMinute; j+=interval) {
                    result.add(getTime(i, j));
                }
            } else {
                if (i == beginHour) {
                    for (int j = beginMinute; j <= MAX_MINUTE; j+=interval) {
                        minuteAdd=j;
                        result.add(getTime(i, j));
                    }
                    minuteAdd+=interval;
                    minuteAdd=jumpMinute(minuteAdd);
                } else if (i == endHour) {
                    for (int j = minuteAdd; j <= endMinute; j+=interval) {
                        minuteAdd=j;
                        result.add(getTime(i, j));

                    }
                } else {
                    for (int j = minuteAdd; j <= MAX_MINUTE; j+=interval) {
                        minuteAdd=j;
                        result.add(getTime(i, j));
                    }
                    minuteAdd+=interval;
                    minuteAdd=jumpMinute(minuteAdd);
                }
            }

        }
        return result;
    }

    /**
     * 获取提前或后面的分钟时间
     *
     * @param minutesStr HH:mm
     * @param addMinutes  间隔的分钟数，可以为正负
     * @return  HH:mm
     */
    public static String getMinute(String minutesStr, int addMinutes) {
        Integer beginHour = Integer.valueOf(minutesStr.substring(0, 2));
        Integer finalMinute = Integer.valueOf(minutesStr.substring(3, 5));

        int addHour = addMinutes / MINUTE;
        int addMinute=addMinutes - (addHour * MINUTE);
        if(addMinutes>0){
            if(addMinute+finalMinute>MAX_MINUTE){
                int tempAddHour = (addMinute + finalMinute) / MINUTE;
                int tempAddMinute = (addMinute + finalMinute) - (tempAddHour * MINUTE);
                addHour+=1;
                finalMinute=tempAddMinute;
            }else {
                finalMinute+=addMinute;
            }
        }else {
            if(addMinute+finalMinute<0){
                addHour-=1;
                finalMinute=addMinute + finalMinute+MINUTE;
            }else {
                finalMinute+=addMinute;
            }
        }

      return   getTime(beginHour+addHour, finalMinute);

    }




    /**
     * 分钟跳过，不支持跳过超过60分钟的   64 转换成4秒
     * @param minuteAdd
     * @return
     */
    private static  int jumpMinute(int minuteAdd){
        if(minuteAdd>MAX_MINUTE){
            return minuteAdd-MAX_MINUTE-1;
        }else {
            return minuteAdd;
        }
    }



    private static String getTime(int hour, int minute) {
        String hourStr="";
        if(hour<MONTH_COVER_MAX){
            hourStr="0"+hour;
        }else {
            hourStr=String.valueOf(hour);
        }
        String minuteStr="";
        if (minute < MONTH_COVER_MAX) {
            minuteStr="0"+minute;
        } else {
            minuteStr=String.valueOf(minute);
        }
        return hourStr+":"+minuteStr;
    }

    /**
     * 拼接YYYY_MM
     * @param year
     * @param month
     * @return
     */
    private static String getDate(int year, int month) {
        if (month < MONTH_COVER_MAX) {
            return year + "-0" + month;
        } else {
            return year + "-" + month;
        }
    }

    /**
     * 获取月分
     *
     * @param dateMonth yyyy-mm
     * @return Integer 获取月分
     */
    private static Integer getMonth(String dateMonth) {
        String month = dateMonth.substring(5, 7);
        if (MONTH_BEGIN_STRING.equals(month.substring(0, 1))) {
            return Integer.valueOf(month.substring(1, 2));
        } else {
            return Integer.valueOf(month);
        }
    }
}
