package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.utils.RedisKeyUtils;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.model.dto.DongFangPlateDTO;
import com.coatardbul.stock.model.entity.AccountBase;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
public class DongFangPlateService {

    @Autowired
    HttpPoolService httpService;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;

    public Object getAllPlate() {

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());
        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://myfavor.eastmoney.com/v4/webouter/ggdefstkindexinfos?" +
                "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                "_=" + l;
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
        if ("0".equals(jsonObject.getString("state"))) {
            return jsonObject.getJSONObject("data").getJSONArray("ginfolist");
        } else {
            return new ArrayList<>();
        }
    }

    public DongFangPlateDTO getPlateStock(DongFangPlateDTO dto) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());

        Assert.notNull(dto.getGid(), "id不能为空");
        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://myfavor.eastmoney.com/v4/webouter/gstkinfos?" +
                "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                "g=" + dto.getGid() + "&" +
                "_=" + l;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return dto;
        }
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("0".equals(jsonObject.getString("state"))) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("stkinfolist");
            List<String> codeArr = new ArrayList<String>();
            for (int i = 0; i < jsonArray.size(); i++) {
                String security = jsonArray.getJSONObject(i).getString("security");
                String[] split = security.split("\\$");
                codeArr.add(split[1]);
            }
            dto.setCodeArr(codeArr);
        }
        return dto;
    }


    public Object deletePlateStock(DongFangPlateDTO dto) {
        Assert.notNull(dto.getGid(), "id不能为空");

        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());


        List<String> newCodeArr = new ArrayList<String>();
        for (String code : dto.getCodeArr()) {
            newCodeArr.add(getCodeUrl(code));
        }
        String codeScs = StringUtils.join(newCodeArr, ",");


        List<Header> headerList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://myfavor.eastmoney.com/v4/webouter/dslot?" +
                "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                "g=" + dto.getGid() + "&" +
                "scs=" + codeScs + "&" +
                "_=" + l;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        return dto;
    }


    public void addPlateInfo(DongFangPlateDTO dto) {
        List<Header> headerList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());

        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());
        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        for (String code : dto.getCodeArr()) {
            long l = System.currentTimeMillis();
            String response = null;
            String url = "http://myfavor.eastmoney.com/v4/webouter/as?" +
                    "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                    "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                    "g=" + dto.getGid() + "&" +
                    "sc=" + getCodeUrl(code) + "&" +
                    "_=" + l;

            try {
                response = httpService.doGet(url, headerList, false);
            } catch (ConnectTimeoutException e) {
                throw new BusinessException("链接异常");
            }
            int i = 1;
        }
    }

    private static String getCodeUrl(String code) {
        String codeUrl = null;
        if (code.substring(0, 2).equals("00") || code.substring(0, 2).equals("30")) {
            codeUrl = "0$" + code;
        } else {
            codeUrl = "1$" + code;
        }
        return codeUrl;
    }

    public List<String> getCodeUrlList(DongFangPlateDTO dto) {
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
        String url = "http://myfavor.eastmoney.com/v4/webouter/gstkinfos?" +
                "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                "g=" + dto.getGid() + "&" +
                "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                "_=" + l;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return null;
        }

        List<String> plateCodeArr = new ArrayList<>();
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("0".equals(jsonObject.getString("state"))) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("stkinfolist");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String security = jsonObject1.getString("security");
                plateCodeArr.add(security.substring(2, 8));
            }
        }
        return plateCodeArr;
    }


    public void sysnPlateInfo(DongFangPlateDTO dto) {

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
        String url = "http://myfavor.eastmoney.com/v4/webouter/gstkinfos?" +
                "appkey=d41d8cd98f00b204e9800998ecf8427e&" +
                "g=" + dto.getGid() + "&" +
                "cb=jQuery33108240025094669978_" + (l - 3) + "&" +
                "_=" + l;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return;
        }

        List<String> plateCodeArr = new ArrayList<>();
        int beginIndex = response.indexOf("(");
        int endIndex = response.lastIndexOf(")");
        response = response.substring(beginIndex + 1, endIndex);
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("0".equals(jsonObject.getString("state"))) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("stkinfolist");
            for (int i = 0; i < jsonArray.size(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String security = jsonObject1.getString("security");
                plateCodeArr.add(security.substring(2, 8));
            }
        }
        //删除当日已经有的
        //获取redis上所有当前时间的key
        Set keys = redisTemplate.keys(RedisKeyUtils.getStockInfoPattern(dto.getDateStr()));
        for (Object codeKey : keys) {
            if (codeKey instanceof String) {
                String code = RedisKeyUtils.getCodeByStockInfoKey(codeKey.toString());
                if (!plateCodeArr.contains(code)) {
                    redisTemplate.delete(codeKey);
                    String hisStockTickInfo = RedisKeyUtils.getHisStockTickInfo(dto.getDateStr(), code);
                    redisTemplate.delete(hisStockTickInfo);
                }
            }
        }
    }

    public Object clearPlateStock(DongFangPlateDTO dto) {
        DongFangPlateDTO plateStock = getPlateStock(dto);
        deletePlateStock(plateStock);
        return dto;
    }
}
