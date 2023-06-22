package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.stock.mapper.StockTradeUserMapper;
import com.coatardbul.stock.model.dto.StockUserCookieDTO;
import com.coatardbul.stock.model.entity.StockTradeUser;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeBaseService {


    private final static String TRADE_USER_COOKIE = "TRADE_USER_COOKIE";
    @Autowired
    StockTradeUserMapper stockTradeUserMapper;
    @Autowired
    HttpPoolService httpService;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    StockUserBaseService stockUserBaseService;



    /**
     * @param url 请求路径
     * @param dto 请求参数
     * @return
     * @throws ConnectTimeoutException
     */
    public String trade(String url, Object dto, String userName) throws ConnectTimeoutException {
        //http请求
        List<Header> headerList = getHeaderList(userName);
        if (headerList == null || headerList.size() == 0) {
            throw new BusinessException("cookie参数异常");
        }
        String params = getRequestParam(dto);
        String result = httpService.doPost(url, params, headerList, false);


        return result;
    }

    public String tradeByString(String url, String param, String userName) throws ConnectTimeoutException {
        //http请求
        List<Header> headerList = getHeaderList(userName);
        if (headerList == null || headerList.size() == 0) {
            throw new BusinessException("cookie参数异常");
        }
        String result = httpService.doPost(url, param, headerList, false);
        return result;
    }


    private List<Header> getHeaderList(String userName) {

        StockTradeUser stockTradeUser = stockTradeUserMapper.selectByPrimaryKey(userName);
        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", stockTradeUser.getCookie());
        Header orign = httpService.getHead("Origin", "https://jywg.18.cn");
        Header contentType = httpService.getHead("Content-Type", "application/x-www-form-urlencoded");
        headerList.add(cookie);
        headerList.add(orign);
        headerList.add(contentType);
        return headerList;

    }


    /**
     * body参数
     *
     * @param obj
     * @return
     */
    public String getRequestParam(Object obj) {
        StringBuffer params = new StringBuffer();
        try {
            Field[] fields = obj.getClass().getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {//遍历
                //得到属性
                Field field = fields[i];
                //打开私有访问
                field.setAccessible(true);
                //获取属性
                String name = field.getName();
                //获取属性值  .get(object)
                Object ownValue = field.get(obj);
                if (ownValue != null && ownValue.toString().length() > 0) {
                    if (params.length() == 0) {
                        params.append(name).append("=").append(ownValue.toString());
                    } else {
                        params.append("&").append(name).append("=").append(ownValue.toString());
                    }
                }
            }
        } catch (IllegalAccessException e) {
            log.error(e.getMessage(), e);
        }
        return params.toString();
    }

    public void updateCookie(StockUserCookieDTO dto) {
        StockTradeUser stockTradeUser = new StockTradeUser();
        stockTradeUser.setId(dto.getId());
        stockTradeUser.setCookie(dto.getCookie());
        Date expireDate = DateTimeUtil.getBeforeDate(-dto.getDuration(), Calendar.MINUTE);
        stockTradeUser.setExpireTime(expireDate);
        stockTradeUserMapper.updateByPrimaryKeySelective(stockTradeUser);

    }
}
