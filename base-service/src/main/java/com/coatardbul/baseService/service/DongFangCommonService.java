package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.constants.UpDwonEnum;
import com.coatardbul.baseService.entity.bo.TickInfo;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/11
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DongFangCommonService extends CommonService implements DataServiceBridge {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HttpService httpService;
    @Autowired(required = false)
    private XinlangCommonService xinlangService;


    @Override
    public void getAndRefreshStockInfo(String code) {
        String response = getStockInfo(code);
        // 将获取的信息更新到code上
        if (StringUtils.isNotBlank(response)) {
            updateStockInfo(code, response, null);
        }
        //获取最新的对象
    }

    @Override
    public void getAndRefreshStockInfo(String code, String dateFormat) {
        updateStockInfo(code, null, dateFormat);
        String key = RedisKeyUtils.getHisStockTickInfo(dateFormat, code);
        String stockTickArrStr = (String) redisTemplate.opsForValue().get(key);
        if(StringUtils.isNotBlank(stockTickArrStr)){
            List<TickInfo> stockTickArr = JsonUtil.readToValue(stockTickArrStr, new TypeReference<List<TickInfo>>() {
            });
            updateStockBaseInfo(stockTickArr, code,dateFormat);
        }

        //获取最新的对象
    }

    @Override
    public String getStockInfo(String code) {
        List<Header> headerList = new ArrayList<>();
        String codeUrl = getCodeUrl(code);
        //东方财富 接口 地址
        setHeadList(headerList);
        long l = System.currentTimeMillis();
        //返回信息
        String response = null;
        try {
            response = httpService.doGet("http://push2.eastmoney.com/api/qt/stock/get?invt=2&fltt=1&cb=jQuery351021675633936193583_" + (l - 1)
                    + "&fields=f58%2Cf734%2Cf107%2Cf57%2Cf43%2Cf59%2Cf169%2Cf170%2Cf152%2Cf177%2Cf111%2Cf46%2Cf60%2Cf44%2Cf45%2Cf47%2Cf260%2Cf48%2Cf261%2Cf279%2Cf277%2Cf278%2Cf288%2Cf19%2Cf17%2Cf531%2Cf15%2Cf13%2Cf11%2Cf20%2Cf18%2Cf16%2Cf14%2Cf12%2Cf39%2Cf37%2Cf35%2Cf33%2Cf31%2Cf40%2Cf38%2Cf36%2Cf34%2Cf32%2Cf211%2Cf212%2Cf213%2Cf214%2Cf215%2Cf210%2Cf209%2Cf208%2Cf207%2Cf206%2Cf161%2Cf49%2Cf171%2Cf50%2Cf86%2Cf84%2Cf85%2Cf168%2Cf108%2Cf116%2Cf167%2Cf164%2Cf162%2Cf163%2Cf92%2Cf71%2Cf117%2Cf292%2Cf51%2Cf52%2Cf191%2Cf192%2Cf262%2Cf294%2Cf295%2Cf269%2Cf270%2Cf256%2Cf257%2Cf285%2Cf286&secid="
                    + codeUrl + "&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=3155316106775332%7C0%7C1%7C0%7Cweb&_=" + l, headerList, cronRefreshService.getProxyFlag());
        } catch (ConnectTimeoutException e) {
        }
        return response;

    }

    ;


    private void setHeadList(List<Header> headerList) {
        Header cookie = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
    }


    /**
     * @param response
     * @param map
     */
    @Override
    public void rebuildStockDetailMap(String response, Map map) {
        if (!StringUtils.isNotBlank(response)) {
            return;
        }

        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONObject data = jsonObject.getJSONObject("data");
        if (data != null) {
            //今日开盘价，竞价价格
            map.put("auctionPrice", getDongFangPrice(data, "f46"));
            //昨日收盘价
            map.put("lastClosePrice", getDongFangPrice(data, "f60"));
            //目前收盘价
            map.put("newPrice", getDongFangPrice(data, "f43"));
            //最高收盘价
            map.put("maxPrice", getDongFangPrice(data, "f44"));
            //最低收盘价
            map.put("minPrice", getDongFangPrice(data, "f45"));
            //未知？？
//            map.put("minPrice", split1[6]);
            //未知？？
//            map.put("minPrice", split1[7]);
            //交易量  需要除以100 ，总手
            map.put("tradeVol", getDongFangValue(data, "f47").multiply(new BigDecimal(100)));
            //成交金额
            map.put("tradeAmount", getDongFangValue(data, "f48"));

            map.put("buy1Price", getDongFangPrice(data, "f19"));
            map.put("buy2Price", getDongFangPrice(data, "f17"));
            map.put("buy3Price", getDongFangPrice(data, "f15"));
            map.put("buy4Price", getDongFangPrice(data, "f13"));
            map.put("buy5Price", getDongFangPrice(data, "f11"));
            map.put("buy1Vol", getDongFangVol(data, "f20"));
            map.put("buy2Vol", getDongFangVol(data, "f18"));
            map.put("buy3Vol", getDongFangVol(data, "f16"));
            map.put("buy4Vol", getDongFangVol(data, "f14"));
            map.put("buy5Vol", getDongFangVol(data, "f12"));

            map.put("sell1Price", getDongFangPrice(data, "f39"));
            map.put("sell2Price", getDongFangPrice(data, "f37"));
            map.put("sell3Price", getDongFangPrice(data, "f35"));
            map.put("sell4Price", getDongFangPrice(data, "f33"));
            map.put("sell5Price", getDongFangPrice(data, "f31"));
            map.put("sell1Vol", getDongFangVol(data, "f40"));
            map.put("sell2Vol", getDongFangVol(data, "f38"));
            map.put("sell3Vol", getDongFangVol(data, "f36"));
            map.put("sell4Vol", getDongFangVol(data, "f34"));
            map.put("sell5Vol", getDongFangVol(data, "f32"));


            //市值
            map.put("marketValue", data.get("f116"));
            //流通市值
            map.put("circulationMarketValue", data.get("f117"));
            //暂时不处理
//            map.put("tradeBuySell5Time", split1[31]);
            calcMap(map);
        }
    }

    private BigDecimal getDongFangValue(JSONObject data, String key) {
        if (data.get(key) == null || "-".equals(data.get(key).toString())) {
            return BigDecimal.ZERO;
        } else {
            return new BigDecimal(data.get(key).toString());
        }
    }

    private BigDecimal getDongFangPrice(JSONObject data, String key) {
        if (data.get(key) == null || "-".equals(data.get(key).toString())) {
            return null;
        } else {
            return new BigDecimal(data.get(key).toString()).divide(new BigDecimal(100));
        }
    }

    private BigDecimal getDongFangVol(JSONObject data, String key) {
        if (data.get(key) == null || "-".equals(data.get(key).toString())) {
            return null;
        } else {
            return new BigDecimal(data.get(key).toString()).multiply(new BigDecimal(100));
        }
    }


    @Override
    public void refreshStockTickInfo(String code) {
        String response = getStockTickInfo(code);
        // 将获取的信息更新到code上
        if (StringUtils.isNotBlank(response)) {
            List<TickInfo> list = updateStockTickInfo(code, response, null);
            try {
                updateStockBaseInfo(list, code,null);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
        //获取最新的对象
    }


    @Override
    public String getStockTickInfo(String code) {
        List<Header> headerList = new ArrayList<>();
        String codeUrl = getCodeUrl(code);
        //东方财富 接口 地址
        setHeadList(headerList);

        long l = System.currentTimeMillis();
        //返回信息
        String response = null;
        try {
            response = httpService.doGet("https://push2.eastmoney.com/api/qt/stock/details/get?fields1=f1,f2,f3,f4&fields2=f51,f52,f53,f54,f55" +
                    "&fltt=2&cb=jQuery3510020623351060268913_" + (l - 1) + "&pos=-5000&secid=" + codeUrl + "&ut=fa5fd1943c7b386f172d6893dbfba10b&wbp2u=3155316106775332%7C0%7C1%7C0%7Cweb&_=" + l, headerList, cronRefreshService.getProxyFlag());
        } catch (ConnectTimeoutException e) {
        }
        return response;

    }


    private static String getCodeUrl(String code) {
        String codeUrl;
        if (code.substring(0, 2).equals("00") || code.substring(0, 3).equals("300")) {
            codeUrl = "0." + code;
        } else {
            codeUrl = "1." + code;
        }
        return codeUrl;
    }

    private List<TickInfo> updateStockTickInfo(String code, String response, String dateFormat) {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();
        if (!StringUtils.isNotBlank(dateFormat)) {
            dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        }
        String key = RedisKeyUtils.getHisStockTickInfo(dateFormat, code);
        List<TickInfo> stockTickDetail = getStockTickDetail(code, response);
        if (stockTickDetail != null && stockTickDetail.size() > 0) {
            redisTemplate.opsForValue().set(key, JsonUtil.toJson(stockTickDetail), cronRefreshConfigBo.getCodeExistHour(), TimeUnit.HOURS);
        }
        return stockTickDetail;
    }

    @Override
    public List<TickInfo> getStockTickDetail(String code, String response) {
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("details");
        List<TickInfo> result = new ArrayList<TickInfo>();
        if (jsonArray.size() > 0) {
            for (int k = 0; k < jsonArray.size(); k++) {
                String itemStr = jsonArray.get(k).toString();
                String[] item = itemStr.split(",");
                try {
                    TickInfo tickInfo = new TickInfo();
                    tickInfo.setTime(item[0]);
                    tickInfo.setVol(new BigDecimal(item[2].trim()));
                    tickInfo.setPrice(new BigDecimal(item[1].trim()));
                    //买入
                    if ("2".equals(item[4])) {
                        tickInfo.setBuySellFlag(UpDwonEnum.UP.getType());
                    }
                    if ("1".equals(item[4])) {
                        tickInfo.setBuySellFlag(UpDwonEnum.DOWN.getType());
                    }
                    result.add(tickInfo);
                } catch (Exception e) {

                }

            }
        }
        return result;

    }


    @Override
    public void refreshStockMinuterInfo(String code) {
        xinlangService.refreshStockMinuterInfo(code);
    }

    @Override
    public String getStockMinuterInfo(String code) {
        return xinlangService.getStockMinuterInfo(code);
    }

    @Override
    public List getStockMinuterDetail(String code, String response) {
        return xinlangService.getStockMinuterDetail(code, response);
    }
}
