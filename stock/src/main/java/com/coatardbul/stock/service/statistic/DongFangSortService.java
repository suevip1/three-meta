package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.trade.StockBaseDetail;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.model.entity.StockBase;
import com.coatardbul.baseCommon.model.entity.StockPrice;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.model.entity.AccountBase;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TradeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/6/1
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DongFangSortService {

    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    HttpPoolService httpService;
    @Autowired
    StockBaseMapper stockBaseMapper;
    @Autowired
    TradeBaseService tradeBaseService;
    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;

    public Object getSpeed() {

        List<Header> headerList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());
        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "https://32.push2.eastmoney.com/api/qt/clist/get?" +
                "cb=jQuery1124029900100396333995_" + (l - 33) +
                "&pn=1&pz=20&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2" +
                "&wbp2u=6751315946175528|0|1|0|web&fid=f11&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048" +
                "&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152" +
                "&" +
                "_=" + l;
        url = url.replaceAll("\\|", "%124");
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new ArrayList<>();
        }

        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String code = jsonObject1.getString("f12");
            StockBase stockBase = stockBaseMapper.selectByPrimaryKey(code);
            if (stockBase != null) {
                jsonObject1.put("概念", stockBase.getTheme());
                jsonObject1.put("行业", stockBase.getIndustry());
            }
        }

        return jsonArray;

    }

    public Object getIncrease() {
        JSONArray jsonArray = getIncreaseResponse(1);
        List<StockBase> stockBases = stockBaseMapper.selectByAll(new StockBase());
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String code = jsonObject1.getString("f12");
            List<StockBase> collect = stockBases.stream().filter(o1 -> code.equals(o1.getCode())).collect(Collectors.toList());
            if(collect!=null &&collect.size()>0){
                StockBase stockBase=collect.get(0);
                if (stockBase != null) {
                    jsonObject1.put("概念", stockBase.getTheme());
                    jsonObject1.put("行业", stockBase.getIndustry());
                }
            }
        }
        return jsonArray;
    }

    public JSONArray getIncreaseResponse(Integer starPage) {
        List<Header> headerList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());
        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://52.push2.eastmoney.com/api/qt/clist/get?" +
                "cb=jQuery11240024725486433821997_" + (l - 33) +
                "&pn=" + starPage + "&pz=200&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2" +
                "&wbp2u=6751315946175528|0|1|0|web&fid=f3&fs=m:0+t:6,m:0+t:80,m:1+t:2,m:1+t:23,m:0+t:81+s:2048" +
                "&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f62,f128,f136,f115,f152" +
                "&" +
                "_=" + l;
        url = url.replaceAll("\\|", "%124");
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new JSONArray();
        }
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");
        return jsonArray;
    }

    /**
     * 获取所有涨幅
     * @param
     * @return
     */
    public JSONArray getIncreaseAll() throws InterruptedException {
        List<StockBase> stockBases = stockBaseMapper.selectByAll(new StockBase());
        int count = stockBases.size() / 200+1;
        JSONArray ja=new JSONArray();
        for(int i=1;i<=count;i++){
            try {
                JSONArray jsonArray = getIncreaseResponse(i);
                Thread.sleep(new Random().nextInt(2*1000));
                ja.addAll(jsonArray);
            }catch (Exception e){
                log.error("获取涨幅排序异常");
            }
        }
        for (int i = 0; i < ja.size(); i++) {
            JSONObject jsonObject1 = ja.getJSONObject(i);
            String code = jsonObject1.getString("f12");
            List<StockBase> collect = stockBases.stream().filter(o1 -> code.equals(o1.getCode())).collect(Collectors.toList());
            if(collect!=null &&collect.size()>0){
                StockBase stockBase=collect.get(0);
                if (stockBase != null) {
                    jsonObject1.put("概念", stockBase.getTheme());
                    jsonObject1.put("行业", stockBase.getIndustry());
                }
            }
        }
        return ja;
    }
    public List<StockPrice> getIncreaseObjAll() throws InterruptedException, ParseException {
        List<StockPrice> result = new ArrayList();
        JSONArray increaseAll = getIncreaseAll();
        String dateFormat = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        if (stockVerifyService.isIllegalDate(dateFormat)) {
            return result;
        }
        for (int i = 0; i < increaseAll.size(); i++) {
            JSONObject jsonObject = increaseAll.getJSONObject(i);
            StockPrice stockPrice=new StockPrice();
            stockPrice.setDateStr(dateFormat);
            stockPrice.setTurnOverRate(getDongFangPrice(jsonObject, "f8"));
            stockPrice.setVolume(Integer.valueOf(jsonObject.getString("f5")));
            stockPrice.setMaxSubRate(getDongFangPrice(jsonObject, "f7"));
            stockPrice.setName(jsonObject.getString("f14"));
            stockPrice.setCode(jsonObject.getString("f12"));
            stockPrice.setTradeAmount(getDongFangPrice(jsonObject, "f6"));
            stockPrice.setLastClosePrice(getDongFangPrice(jsonObject, "f18"));
            stockPrice.setOpenPrice(getDongFangPrice(jsonObject, "f17"));
            stockPrice.setMaxPrice(getDongFangPrice(jsonObject, "f15"));
            stockPrice.setMinPrice(getDongFangPrice(jsonObject, "f16"));
            stockPrice.setClosePrice(getDongFangPrice(jsonObject, "f2"));
            stockPrice.setCurrIncreaseRate(new BigDecimal(jsonObject.getString("f3")));

            stockPrice.setId(stockPrice.getDateStr()+"_"+stockPrice.getCode());

        }
        return result;
    }

    public List<StockBaseDetail> getConvertBondLimit(StockStrategyQueryDTO dto) {

        return getConvertBondLimit(null, null, dto.getOrderStr());
    }

    public List<StockBaseDetail> getAllConvertBond() {

        List<StockBaseDetail> result = new ArrayList();
        JSONArray jsonArray = getConvertBondCommon(1, 1000);
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            StockBaseDetail stockDetail = getStockBaseDetail(jsonObject1);
            try {
                tradeBaseService.rebuild(stockDetail);
            } catch (Exception e) {
                log.error(stockDetail.getName() + "重构异常");
                log.error(e.getMessage(), e);
            }
            result.add(stockDetail);
        }

        return result;
    }

    /**
     * 获取前20 转债
     *
     * @param page
     * @param pageSize
     * @return
     */
    public List<StockBaseDetail> getConvertBondLimit(Integer page, Integer pageSize, String orderStr) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 50;
        }
        List<StockBaseDetail> result = new ArrayList();
        JSONArray jsonArray = getConvertBondCommon(page, pageSize, orderStr);
        for (int i = 0; i < 50; i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            StockBaseDetail stockDetail = getStockBaseDetail(jsonObject1);
            tradeBaseService.rebuild(stockDetail);
            StockBase stockBase = stockBaseMapper.selectByPrimaryKey(stockDetail.getCode());
            if (stockBase != null) {
                stockDetail.setIndustry(stockBase.getIndustry());
                stockDetail.setTheme(stockBase.getTheme());
            }
            result.add(stockDetail);
        }

        return result;
    }

    public StockBaseDetail getStockBaseDetail(JSONObject jsonObject1) {
        StockBaseDetail stockDetail = new StockBaseDetail();
        try {
            stockDetail.setName(jsonObject1.getString("f14"));
            stockDetail.setCode(jsonObject1.getString("f12"));
            stockDetail.setConvertName(jsonObject1.getString("f234"));
            stockDetail.setConvertCode(jsonObject1.getString("f232"));
            stockDetail.setTradeAmount(getDongFangPrice(jsonObject1, "f6"));
            stockDetail.setLastClosePrice(getDongFangPrice(jsonObject1, "f18"));
            stockDetail.setCallAuctionPrice(getDongFangPrice(jsonObject1, "f17"));
            stockDetail.setMaxPrice(getDongFangPrice(jsonObject1, "f15"));
            stockDetail.setMinPrice(getDongFangPrice(jsonObject1, "f16"));
            stockDetail.setCurrPrice(getDongFangPrice(jsonObject1, "f2"));
            stockDetail.setFiveIncreaseRate(getDongFangPrice(jsonObject1, "f11"));
            stockDetail.setCurrUpRate(new BigDecimal(jsonObject1.getString("f3")).divide(new BigDecimal(100), 4, BigDecimal.ROUND_HALF_UP));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return stockDetail;
    }

    private BigDecimal getDongFangPrice(JSONObject data, String key) {
        if (data.get(key) == null || "-".equals(data.get(key).toString())) {
            return null;
        } else {
            return new BigDecimal(data.get(key).toString());

        }
    }

    public JSONArray getConvertBondCommon(Integer page, Integer pageSize) {

        return getConvertBondCommon(page, pageSize, "f3");
    }

    /**
     * f3涨幅排序 f6金额排序
     *
     * @param page
     * @param pageSize
     * @param orderStr
     * @return
     */
    public JSONArray getConvertBondCommon(Integer page, Integer pageSize, String orderStr) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = 50;
        }
        List<StockBaseDetail> result = new ArrayList();
        List<Header> headerList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());

        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        //f3涨幅， f6 交易金额
        String url = "http://60.push2.eastmoney.com/api/qt/clist/get?" +
                "cb=jQuery112406253327725026963_" + (l - 33) +
                "&pn=" + page + "&pz=" + pageSize + "&po=1&np=1&ut=bd1d9ddb04089700cf9c27f6f7426281&fltt=2&invt=2" +
                "&wbp2u=6751315946175528|0|1|0|web&fid=" + orderStr + "&fs=b:MK0354" +
                "&fields=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13,f14,f15,f16,f17,f18,f20,f21,f23,f24,f25,f22,f11,f62,f128,f136,f115,f152,f234,f232" +
                "&" +
                "_=" + l;
        url = url.replaceAll("\\|", "%124");
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new JSONArray();
        }

        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("diff");


        return jsonArray;
    }


}
