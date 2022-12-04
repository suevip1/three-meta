package com.coatardbul.baseService.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
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
import java.util.HashMap;
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
public abstract class XinlangCommonService extends CommonService
        implements DataServiceBridge {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HttpPoolService httpService;


    @Autowired
    StockUpLimitAnalyzeCommonService stockUpLimitAnalyzeCommonService;




    @Override
    public void getAndRefreshStockInfo(String code) {

        String response = getStockInfo(code);
        // 将获取的信息更新到code上
        if (StringUtils.isNotBlank(response)) {
            updateStockInfo(code, response,null);
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
    public  String getStockInfo(String code){
        List<Header> headerList = new ArrayList<>();
        String codeUrl = getCodeUrl(code);
        //新浪财经接口 地址
        setHeadList(headerList, codeUrl);
        //返回信息
        String response = null;
        try {
            response = httpService.doGet("https://hq.sinajs.cn/rn=" + System.currentTimeMillis() + "&list=" + codeUrl + "," + codeUrl + "_i,bk_new_qtxy", headerList, cronRefreshService.getProxyFlag());
        } catch (ConnectTimeoutException e) {
        }
        return response;
    }



    private static String getCodeUrl(String code) {
        String codeUrl = null;
        if (code.substring(0, 2).equals("00") || code.substring(0, 3).equals("300")) {
            codeUrl = "sz" + code;
        } else {
            codeUrl = "sh" + code;
        }
        return codeUrl;
    }

    @Override
    public void refreshStockTickInfo(String code) {
        String response = getStockTickInfo(code);
        // 将获取的信息更新到code上
        if (StringUtils.isNotBlank(response)) {
            List<TickInfo> list = updateStockTickInfo(code, response);
            try {
                updateStockBaseInfo(list, code,null);
            } catch (Exception e) {
            }
        }
    }


    @Override
    public String getStockTickInfo(String code){
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();

        List<Header> headerList = new ArrayList<>();
        String codeUrl = "";
        codeUrl = getCodeUrl(code);
        //新浪财经接口 地址
        setHeadList(headerList, codeUrl);
        //返回信息
        String response = null;
        try {
            response = httpService.doGet("https://vip.stock.finance.sina.com.cn/quotes_service/view/CN_TransListV2.php?num=5000&symbol=" + codeUrl, headerList, cronRefreshConfigBo.getIsProxy());
        } catch (ConnectTimeoutException e) {

        }
        return response;
    }

    @Override
    public void refreshStockMinuterInfo(String code) {

        String response = getStockMinuterInfo(code);
        // 将获取的信息更新到code上
        updateStockMinuterInfo(code, response);
        //获取最新的对象
    }

    @Override
    public String getStockMinuterInfo(String code){
        List<Header> headerList = new ArrayList<>();
        String codeUrl = getCodeUrl(code);

        setHeadList(headerList, codeUrl);
        //返回信息
        String response = null;
        try {
            response = httpService.doGet("https://quotes.sina.cn/cn/api/openapi.php/CN_MinlineService.getMinlineData?symbol=" + codeUrl, headerList, cronRefreshService.getProxyFlag());
        } catch (ConnectTimeoutException e) {
        }
        return response;
    }

    private void setHeadList(List<Header> headerList, String codeUrl) {
        //新浪财经接口 地址
        Header cookie = httpService.getHead("Referer", "https://finance.sina.com.cn/realstock/company/" + codeUrl + "/nc.shtml");
        headerList.add(cookie);
    }

    private void updateStockMinuterInfo(String code, String response) {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();
        String key = "minuter_" + code;
        List stockTickDetail = getStockMinuterDetail(code, response);
        if (stockTickDetail != null && stockTickDetail.size() > 0) {
            redisTemplate.opsForValue().set(key, JsonUtil.toJson(stockTickDetail), cronRefreshConfigBo.getCodeExistHour(), TimeUnit.HOURS);
        }
    }


    private List<TickInfo> updateStockTickInfo(String code, String response) {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();
        String key = RedisKeyUtils.getNowStockTickInfo(code);
        List<TickInfo> stockTickDetail = getStockTickDetail(code, response);
        if (stockTickDetail != null && stockTickDetail.size() > 0) {
            redisTemplate.opsForValue().set(key, JsonUtil.toJson(stockTickDetail), cronRefreshConfigBo.getCodeExistHour(), TimeUnit.HOURS);
        }
        return stockTickDetail;
    }

    @Override
    public List getStockMinuterDetail(String code, String response) {
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("result").getJSONArray("data");
        List<Map> result = new ArrayList<Map>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            JSONObject item = jsonArray.getJSONObject(i);
            map.put("price", item.get("p"));
            map.put("vol", item.get("v"));
            map.put("minuter", item.get("m"));
            map.put("avgPrice", item.get("avg_p"));
            result.add(map);
        }
        return result;
    }

    @Override
    public  List<TickInfo> getStockTickDetail(String code, String response) {
        List<TickInfo> result = new ArrayList<TickInfo>();
        String[] split = response.split("\\n");
        for (int i = split.length - 1; i > 0; i--) {
            try {
                TickInfo tickInfo= new TickInfo();
                String[] item = split[i].split("\\(")[1].split("\\)")[0].replace("'", "").split(",");
                tickInfo.setTime(item[0]);
                tickInfo.setVol( new BigDecimal(item[1].trim()).divide(new BigDecimal(100)));
                tickInfo.setPrice(new BigDecimal(item[2].trim()));
                tickInfo.setBuySellFlag(item[3]);
                result.add(tickInfo);
            } catch (Exception e) {
            }
        }
        return result;

    }




    /**
     * @param response
     * @param map
     */
    @Override
    public void rebuildStockDetailMap(String response, Map map) {
        if(!StringUtils.isNotBlank(response)){
            return;
        }
        String[] split = response.split("\\n");
        if (split.length > 0) {
            String[] split1 = split[0].split("\"")[1].split(",");
            //名称
//            map.put("name", split1[0]);
            //今日开盘价，竞价价格
            map.put("auctionPrice", split1[1]);
            //昨日收盘价
            map.put("lastClosePrice", split1[2]);
            //目前收盘价
            map.put("newPrice", split1[3]);
            //最高收盘价
            map.put("maxPrice", split1[4]);
            //最低收盘价
            map.put("minPrice", split1[5]);
            //未知？？
//            map.put("minPrice", split1[6]);
            //未知？？
//            map.put("minPrice", split1[7]);
            //交易量  需要除以100 ，总手
            map.put("tradeVol", split1[8]);
            //成交金额
            map.put("tradeAmount", split1[9]);

            map.put("buy1Price", split1[11]);
            map.put("buy2Price", split1[13]);
            map.put("buy3Price", split1[15]);
            map.put("buy4Price", split1[17]);
            map.put("buy5Price", split1[19]);
            map.put("buy1Vol", split1[10]);
            map.put("buy2Vol", split1[12]);
            map.put("buy3Vol", split1[14]);
            map.put("buy4Vol", split1[16]);
            map.put("buy5Vol", split1[18]);

            map.put("sell1Price", split1[21]);
            map.put("sell2Price", split1[23]);
            map.put("sell3Price", split1[25]);
            map.put("sell4Price", split1[27]);
            map.put("sell5Price", split1[29]);
            map.put("sell1Vol", split1[20]);
            map.put("sell2Vol", split1[22]);
            map.put("sell3Vol", split1[24]);
            map.put("sell4Vol", split1[26]);
            map.put("sell5Vol", split1[28]);
            map.put("tradeBuySell5Time", split1[31]);
            calcMap(map);
        }
    }


}
