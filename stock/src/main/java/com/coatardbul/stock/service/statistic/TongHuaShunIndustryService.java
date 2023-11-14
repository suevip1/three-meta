package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.TongHuaShunUtil;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/10
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class TongHuaShunIndustryService {

    @Autowired
    HttpPoolService httpService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    SnowFlakeService snowFlakeService;
    @Autowired
    StockStrategyCommonService stockStrategyCommonService;
    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;

    public JSONObject getYear(String yearStr, String bkCode) throws ScriptException, FileNotFoundException, NoSuchMethodException {

        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header orign = httpService.getHead("Referer", "http://q.10jqka.com.cn/");

        headerList.add(cookie);
        headerList.add(orign);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://d.10jqka.com.cn/v4/line/bk_" + bkCode + "/01/" + yearStr + ".js";
        try {
            response = httpService.doGet(url, headerList, true);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return null;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = JSONObject.parseObject(response.split("\\(")[1].split("\\)")[0]);
            return jsonObject;
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public JSONObject getYearResult(String yearStr, String bkCode) {
        JSONObject object = null;
        int retryNum = 5;
        while (retryNum > 0) {
            try {
                object = getYear(yearStr, bkCode);
                if (object == null) {
                    retryNum--;
                } else {
                    break;
                }
            } catch (Exception e) {
                retryNum--;
                log.error(e.getMessage(), e);
            }
        }
        return object;
    }


    public void getAllInfo() {
        List<Map> list = new ArrayList<Map>();
        for (int i = 881101; i < 881200; i++) {
            String name = getBaseInfo(String.valueOf(i));
            if (StringUtils.isNotBlank(name)) {
                Map<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(i));
                map.put("name", name);
                list.add(map);
            }
        }
        for (Map map : list) {
            log.info(snowFlakeService.getSnowId() + "    " + "tongHuaShun_industry_code" + "    " + map.get("id") + "    " + map.get("name"));
        }
    }


    public String getBaseInfo(String codeStr) {
        JSONObject yearResult = getYearResult("last", codeStr);
        if (yearResult == null) {
            return null;
        }
        String data = yearResult.getString("data");
        if (!StringUtils.isNotBlank(data)) {
            return null;
        }


        return yearResult.getString("name");
    }


    public BigDecimal getIncreaseRate(String codeStr, String dateStr) {
        String yearStr = dateStr.substring(0, 4);
        String dateNoSplitStr = dateStr.replaceAll("-", "");
        JSONObject yearResult = getYearResult(yearStr, codeStr);
        if (yearResult == null) {
            return BigDecimal.ZERO;
        }
        String data = yearResult.getString("data");
        if (!StringUtils.isNotBlank(data)) {
            return BigDecimal.ZERO;
        }
        String[] split = data.split(";");
        for (int i = 1; i < split.length; i++) {
            //一次顺序为 日期，开盘，最高，最低，收盘，成交量，成交金额
            String[] currInfo = split[i].split(",");
            if (dateNoSplitStr.equals(currInfo[0])) {
                String[] lastInfo = split[i - 1].split(",");
                return new BigDecimal(currInfo[4]).subtract(new BigDecimal(lastInfo[4])).multiply(new BigDecimal(100)).divide(new BigDecimal(lastInfo[4]), 2, BigDecimal.ROUND_CEILING);
            }
        }

        return BigDecimal.ZERO;
    }

    public List<Map<String, String>> getYearIncreaseRate(String bkCode, String yearStr) {
        List<Map<String, String>>list=new ArrayList<>();
        JSONObject yearResult = getYearResult(yearStr, bkCode);
        if (yearResult == null) {
            return list;
        }
        String data = yearResult.getString("data");
        if (!StringUtils.isNotBlank(data)) {
            return list;
        }
        String[] split = data.split(";");
        for (int i = 1; i < split.length; i++) {
            Map<String, String> map = new HashMap<>();
            //一次顺序为 日期，开盘，最高，最低，收盘，成交量，成交金额
            String[] currInfo = split[i].split(",");
            String[] lastInfo = split[i - 1].split(",");
            BigDecimal bigDecimal= new BigDecimal(currInfo[4]).subtract(new BigDecimal(lastInfo[4])).multiply(new BigDecimal(100)).divide(new BigDecimal(lastInfo[4]), 2, BigDecimal.ROUND_CEILING);
            map.put("increaseRate", bigDecimal.toString());
            BigDecimal sb=null;
            try {
                sb= new BigDecimal(currInfo[2]);
            }catch (NumberFormatException e){
                sb=BigDecimal.ZERO;
                log.error(e.getMessage(),e);
            }
            BigDecimal bigDecimal1= sb.subtract(new BigDecimal(lastInfo[4])).multiply(new BigDecimal(100)).divide(new BigDecimal(lastInfo[4]), 2, BigDecimal.ROUND_CEILING);
            map.put("maxIncreaseRate", bigDecimal1.toString());
            map.put("dateStr",currInfo[0]);
            list.add(map);
        }

        return list;
    }
}
