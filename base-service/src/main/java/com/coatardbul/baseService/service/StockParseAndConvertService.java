package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * <p>
 * Note: 基础的解析和金额，百分比转换
 * <p>
 * Date: 2022/4/19
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockParseAndConvertService {


    public String getStockName(JSONObject jo){
        return  jo.getString("股票简称");
    }

    public String getStockCode(JSONObject jo){
        return  jo.getString("code");
    }






    /**
     * 获取金额
     * @param money
     * @return
     */
    public String getMoneyFormat(BigDecimal money){
        String moneyStr="";
        if(money==null){
            return "null";
        }
        BigDecimal divide = money.divide(new BigDecimal(10000*10000), 2, BigDecimal.ROUND_HALF_DOWN);
        if(divide.compareTo(BigDecimal.ONE)>0){
            moneyStr=divide+"亿";
        }else {
            BigDecimal divide1 = money.divide(new BigDecimal(10000), 2, BigDecimal.ROUND_HALF_DOWN);
            moneyStr=divide1+"万";
        }
        return  moneyStr;
    }
    public String getIncreaseRateFormat(BigDecimal increaseRate){
        String increaseRateFormat="";
        if(increaseRate==null){
            return "null";
        }
        increaseRateFormat= increaseRate.setScale(2, BigDecimal.ROUND_HALF_UP)+"%";
        return  increaseRateFormat;
    }

    /**
     * 转换金额
     * @param value
     * @return
     */
    public BigDecimal convert(Object value) {
        if (value instanceof Integer) {
            return new BigDecimal((Integer) value);
        }
        if (value instanceof Long) {
            return new BigDecimal((Long) value);
        }
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof String) {
            return new BigDecimal((String) value);
        }
        return (BigDecimal) value;
    }


    public BigDecimal getUpLimit(String code,BigDecimal price){
        BigDecimal multiply = null;
        if(code.substring(0,2).equals("30") ||code.substring(0,2).equals("68")){
            multiply=new BigDecimal(1.2);
        }else if(code.substring(0,2).equals("60") ||code.substring(0,2).equals("00")){
            multiply=new BigDecimal(1.1);
        }else {
            multiply=new BigDecimal(1.3);
        }
        BigDecimal result = price.multiply(multiply).setScale(2,BigDecimal.ROUND_HALF_UP);
        return  result;
    }

    public BigDecimal getDownLimit(String code ,BigDecimal price){
        BigDecimal multiply = null;
        if(code.substring(0,2).equals("30") ||code.substring(0,2).equals("68")){
            multiply=new BigDecimal(0.8);
        }else if(code.substring(0,2).equals("60") ||code.substring(0,2).equals("00")){
            multiply=new BigDecimal(0.9);
        }else {
            multiply=new BigDecimal(0.7);
        }
        BigDecimal result = price.multiply(multiply).setScale(2,BigDecimal.ROUND_HALF_UP);
        return  result;
    }



}
