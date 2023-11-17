package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.TongHuaShunUtil;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.es.EsIndustryDataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.script.ScriptException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:同花顺板块，请求历史数据，可以按照年去请求，请求当日数据（如果在交易日，必须得用当日的接口）
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

    @Autowired
    RiverRemoteService riverRemoteService;

    @Autowired
    EsIndustryDataService esIndustryDataService;

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


    public EsIndustryDataBo getTodayResult(String bkCode) {
        EsIndustryDataBo object = null;
        int retryNum = 5;
        while (retryNum > 0) {
            try {
                object = getTodayIncreaseRate(bkCode);
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

    /**
     * 获取当日实时数据,当日数据中有名称
     *
     * @param bkCode
     * @return
     */
    public EsIndustryDataBo getTodayIncreaseRate(String bkCode) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        EsIndustryDataBo convert=null;
        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header orign = httpService.getHead("Referer", "http://q.10jqka.com.cn/");

        headerList.add(cookie);
        headerList.add(orign);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://d.10jqka.com.cn/v4/line/bk_" + bkCode + "/01/today.js";
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
            JSONObject newValue = jsonObject.getJSONObject(bkCode);
            convert = convert(newValue,bkCode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return convert;
    }

    /**
     * 将今日数据转换成标砖数据
     *
     * @return
     */
    private EsIndustryDataBo convert(JSONObject jsonObject, String bkCode) throws IOException {
        EsIndustryDataBo esIndustryDataBo = new EsIndustryDataBo();
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYYMMDD);
        if (StringUtils.isNotBlank(jsonObject.getString(""))) {
            esIndustryDataBo.setYearStr(dateFormat.substring(0, 4));
            esIndustryDataBo.setDateStr(dateFormat);
            esIndustryDataBo.setBkCode(bkCode);
            esIndustryDataBo.setBkName(jsonObject.getString("name"));
            esIndustryDataBo.setOpenValue(jsonObject.getString("7"));
            esIndustryDataBo.setMaxValue(jsonObject.getString("8"));
            esIndustryDataBo.setMinValue(jsonObject.getString("9"));
            esIndustryDataBo.setCloseValue(jsonObject.getString("11"));

            //获取昨日
            String lastDateStr = riverRemoteService.getSpecialDay(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD), -1).replaceAll("-", "");
            //查询昨日es数据
            EsIndustryDataBo esTemplateConfigQuery = new EsIndustryDataBo();
            esTemplateConfigQuery.setYearStr(lastDateStr.substring(0, 4));
            esTemplateConfigQuery.setBkCode(esIndustryDataBo.getBkCode());
            esTemplateConfigQuery.setBkName(esIndustryDataBo.getBkName());
            EsIndustryDataBo lastData = esIndustryDataService.getSingData(esIndustryDataBo);
            esIndustryDataBo.setLastCloseValue(jsonObject.getString("7"));
            String lastCloseValue = null;
            if (lastData != null && lastData.getCloseValue() != null) {
                lastCloseValue = lastData.getCloseValue();
            } else {
                //  不追求数据的准确性，可以用开盘价代替
                lastCloseValue = esIndustryDataBo.getOpenValue();
            }
            esIndustryDataBo.setIncreaseRate(getDivideIncreaseRate(esIndustryDataBo.getIncreaseRate(), lastCloseValue));
            esIndustryDataBo.setMaxIncreaseRate(getDivideIncreaseRate(esIndustryDataBo.getMaxIncreaseRate(), lastCloseValue));
            esIndustryDataBo.setId(esIndustryDataBo.getBkCode() + "_" + esIndustryDataBo.getDateStr());
        }
        return esIndustryDataBo;

    }

    /**
     * 按年获取数据
     *
     * @param bkCode
     * @param yearStr
     * @return
     */
    public List<EsIndustryDataBo> getYearIncreaseRate(String bkCode, String yearStr) {
        List<EsIndustryDataBo> list = new ArrayList<>();
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
            //一次顺序为 日期，开盘，最高，最低，收盘，成交量，成交金额
            String[] currInfo = split[i].split(",");
            String[] lastInfo = split[i - 1].split(",");
            BigDecimal increaseRate = new BigDecimal(currInfo[4]).subtract(new BigDecimal(lastInfo[4])).multiply(new BigDecimal(100)).divide(new BigDecimal(lastInfo[4]), 2, BigDecimal.ROUND_CEILING);
            BigDecimal sb = null;
            try {
                sb = new BigDecimal(currInfo[2]);
            } catch (NumberFormatException e) {
                sb = BigDecimal.ZERO;
                log.error("同花顺行业转换涨幅BigDecimal异常，已设置默认值");
            }
            BigDecimal maxIncreaseRate = sb.subtract(new BigDecimal(lastInfo[4])).multiply(new BigDecimal(100)).divide(new BigDecimal(lastInfo[4]), 2, BigDecimal.ROUND_CEILING);
            EsIndustryDataBo esIndustryDataBo = new EsIndustryDataBo();
            esIndustryDataBo.setYearStr(yearStr);
            esIndustryDataBo.setDateStr(currInfo[0]);
            esIndustryDataBo.setBkCode(bkCode);
            esIndustryDataBo.setIncreaseRate(increaseRate.toString());
            esIndustryDataBo.setMaxIncreaseRate(maxIncreaseRate.toString());
            esIndustryDataBo.setOpenValue(currInfo[1]);
            esIndustryDataBo.setMaxValue(currInfo[2]);
            esIndustryDataBo.setMinValue(currInfo[3]);
            esIndustryDataBo.setCloseValue(currInfo[4]);
            esIndustryDataBo.setLastCloseValue(lastInfo[4]);
            list.add(esIndustryDataBo);
        }
        return list;
    }

    private String getDivideIncreaseRate(String curr, String last) {
        return new BigDecimal(curr).subtract(new BigDecimal(last)).multiply(new BigDecimal(100)).divide(new BigDecimal(last), 2, BigDecimal.ROUND_CEILING).toString();
    }
}
