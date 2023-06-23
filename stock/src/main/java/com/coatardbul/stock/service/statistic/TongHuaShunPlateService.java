package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseCommon.util.TongHuaShunUtil;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.model.dto.TongHuaShunPlateDTO;
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

import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

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
public class TongHuaShunPlateService {

    @Autowired
    HttpPoolService httpService;
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StockStrategyCommonService stockStrategyCommonService;
    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;

    public Object getAllPlate() throws ScriptException, FileNotFoundException, NoSuchMethodException {

        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header hexin = httpService.getHead("hexin-v", heXinStr);
        Header orign =httpService.getHead ("Origin", "http://www.iwencai.com");

        headerList.add(cookie);
        headerList.add(hexin);
        headerList.add(orign);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "http://www.iwencai.com/unifiedwap/self-stock/plate/list" ;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        if (response == null) {
            return new ArrayList<>();
        }
        JSONObject jsonObject = JSONObject.parseObject(response);
        if ("true".equals(jsonObject.getString("success"))) {
            return jsonObject.getJSONArray("data");
        } else {
            return new ArrayList<>();
        }
    }

    public TongHuaShunPlateDTO getPlateStock(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        Assert.notNull(dto.getSn(), "id不能为空");
        Object allPlate = getAllPlate();
        if(allPlate instanceof  JSONArray){
            JSONArray allPlate1 = (JSONArray) allPlate;
            for(int i=0;i<allPlate1.size();i++){
                JSONArray list = allPlate1.getJSONObject(i).getJSONArray("list");
                List<String> codeArr=new ArrayList<>();
                for(int j=0;j<list.size();j++){
                    String stock = list.getJSONObject(j).getString("stock");
                    codeArr.add(stock);
                }
                dto.setCodeArr(codeArr);
            }
        }
        return dto;
    }



    public Object deletePlateStock(TongHuaShunPlateDTO dto) {
        Assert.notNull(dto.getSn(), "id不能为空");

        List<String>newCodeArr=new ArrayList<String>();
        for(String code:dto.getCodeArr()){
            newCodeArr.add(getCodeUrl(code));
        }
        String codeScs=  StringUtils.join(newCodeArr,",");


        List<Header> headerList = new ArrayList<>();
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_NORMAL.getType());

        Header cookie = httpService.getHead("Cookie", accountBase.getCookie());        Header referer = httpService.getHead("Referer", "http://quote.eastmoney.com/");
        headerList.add(cookie);
        headerList.add(referer);
        long l = System.currentTimeMillis();
        String response = null;
        String url = "" ;
        try {
            response = httpService.doGet(url, headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        return dto;
    }


    public void addPlateInfo(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        List<Header> headerList = new ArrayList<>();
        String heXinStr = TongHuaShunUtil.getHeXinStr();
        Header cookie = httpService.getHead("Cookie", stockStrategyCommonService.getCookieValue() + heXinStr);
        Header hexin = httpService.getHead("hexin-v", heXinStr);
        Header orign =httpService.getHead ("Origin", "http://www.iwencai.com");

        headerList.add(cookie);
        headerList.add(hexin);
        headerList.add(orign);

        List<String>newCodeArr=new ArrayList<>();
        for (String code : dto.getCodeArr()) {
            newCodeArr.add( covertCode(code));
        }
        String join = String.join("|", newCodeArr);
        dto.setCodes(join);
        String response = null;
        String url = "http://www.iwencai.com/iwencai/userinfo/iwc/userinfo/self-stock/plate/add" ;
        try {
            response = httpService.doPost(url,JsonUtil.toJson(dto), headerList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("链接异常");
        }
        int i = 1;
    }

    private String covertCode(String code){
        if("00".equals(code.substring(0,2))){
            return code+"_33";
        }
        else if("30".equals(code.substring(0,2))){
            return code+"_33";
        }
        else if("60".equals(code.substring(0,2))){
            return code+"_22";
        }
       else if("68".equals(code.substring(0,2))){
            return code+"_17";
        }
        else {
            return code+"_151";
        }
    }

    private static String getCodeUrl(String code) {
        String codeUrl = null;
        if (code.substring(0, 2).equals("00") || code.substring(0, 3).equals("300")) {
            codeUrl = "0$" + code;
        } else {
            codeUrl = "1$" + code;
        }
        return codeUrl;
    }

    public List<String> getCodeUrlList(TongHuaShunPlateDTO dto) {
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
        String url = "" + l;
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



    public Object clearPlateStock(TongHuaShunPlateDTO dto) throws ScriptException, FileNotFoundException, NoSuchMethodException {
        TongHuaShunPlateDTO plateStock = getPlateStock(dto);
        deletePlateStock(plateStock);
        return dto;
    }
}
