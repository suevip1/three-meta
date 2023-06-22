package com.coatardbul.stock.service.statistic.trade;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.TradeSignEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.util.RSAUtil;
import com.coatardbul.baseService.client.TradeClient;
import com.coatardbul.baseService.entity.bo.HttpConfigBo;
import com.coatardbul.baseService.entity.bo.HttpResponseInfo;
import com.coatardbul.baseService.service.CronRefreshService;
import com.coatardbul.baseService.service.HttpPoolService;
import com.coatardbul.stock.mapper.StockTradeUrlMapper;
import com.coatardbul.stock.mapper.StockTradeUserMapper;
import com.coatardbul.stock.model.dto.StockTradeLoginDTO;
import com.coatardbul.stock.model.dto.StockUserCookieDTO;
import com.coatardbul.stock.model.entity.StockTradeUrl;
import com.coatardbul.stock.model.entity.StockTradeUser;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.codec.binary.Base64.encodeBase64;

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
public class StockTradeUserService {


    @Autowired
    StockTradeBaseService stockTradeBaseService;
    @Autowired
    StockTradeUrlMapper stockTradeUrlMapper;
    @Autowired
    StockTradeUserMapper stockTradeUserMapper;
    @Autowired
    HttpPoolService httpService;
    @Autowired
    CronRefreshService cronRefreshService;
    @Autowired
    TradeClient tradeClient;

    @Autowired
    StockUserBaseService stockUserBaseService;
    public void updateCookie(StockUserCookieDTO dto) {
        if (StringUtils.isNotBlank(dto.getCookie())) {
            stockTradeBaseService.updateCookie(dto);
        }
        stockTradeUrlMapper.updateValidateKey(dto.getValidatekey());
    }

    public void autoLogin(HttpServletRequest request) {
        String userName = stockUserBaseService.getCurrUserName(request);
        if (StringUtils.isBlank(userName)) {
            StockTradeLoginDTO dto=new StockTradeLoginDTO();
            dto.setId(userName);
            login(dto,false);
        }

    }

    public void login(StockTradeLoginDTO dto) {
        login(dto, false);
    }

    public void login(StockTradeLoginDTO dto, boolean autoLoginFlag) {
        StockTradeUser stockTradeUser = null;
        if (autoLoginFlag) {
            //登陆账号
            List<StockTradeUser> stockTradeUsers = stockTradeUserMapper.selectAll();
            stockTradeUser = stockTradeUsers.get(0);
        } else {
            stockTradeUser = stockTradeUserMapper.selectByPrimaryKey(dto.getId());
        }
        //登陆路径
        List<StockTradeUrl> stockTradeUrls = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.LOGIN.getSign());
        if (stockTradeUrls == null || stockTradeUrls.size() == 0) {
            return;
        }
        StockTradeUrl stockTradeUrl = stockTradeUrls.get(0);
        //打开cookie
        tradeClient.openSession();
        TradeClient.ClientWrapper current = tradeClient.getCurrent();
        //请求头
        List<Header> headerList = getHeadList();
        //登陆请求参数
        Map<String, Object> params = null;
        if (autoLoginFlag) {
            //验证码
            StockTradeLoginDTO randomAndKey = getRandomAndIdentifyCode();
            params = getLoginParams(stockTradeUser, randomAndKey);
        } else {
            params = getLoginParams(stockTradeUser, null, dto);
        }
        //post
        HttpPost httpPost = httpService.setHttpPost(stockTradeUrl.getUrl(), "", headerList);
        httpService.setPostMapEntity(httpPost, params);
        //配置
        HttpConfigBo httpConfigBo = new HttpConfigBo();
        httpConfigBo.setProxy(null);
        httpConfigBo.setHttpClient(current.getHttpClient());
        httpConfigBo.setHttpRequestBase(httpPost);

        HttpResponseInfo httpResponseInfo = httpService.executeHttpRequest(httpConfigBo, false);
        if (HttpStatus.SC_OK == httpResponseInfo.getHttpStatus()) {
            String responseStr = httpResponseInfo.getResponseStr();
            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            String status = jsonObject.getString("Status");
            if (!"0".equals(status)) {
                throw new BusinessException("登陆参数信息错误，登陆失败");
            }
        } else {
            throw new BusinessException("http返回码异常，登陆失败");
        }

        //登陆key
        List<StockTradeUrl> loginKey = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.LOGIN_VALIDATEKEY.getSign());
        if (loginKey == null || loginKey.size() == 0) {
            return;
        }
        List<Header> newHeaderList = new ArrayList<>();
        Header cookie = httpService.getHead("Cookie", tradeClient.getCurrentCookie());
        newHeaderList.add(cookie);
        String content = null;
        try {
            content = httpService.doGet(loginKey.get(0).getUrl(), newHeaderList, false);
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("登陆失败");
        }
        String validateKey = getValidateKey(content);


        //更新cookie和key
        StockUserCookieDTO stockUserCookieDTO = new StockUserCookieDTO();
        stockUserCookieDTO.setId(stockTradeUser.getId());
        if (autoLoginFlag) {
            stockUserCookieDTO.setDuration(Integer.valueOf("1800"));
        } else {
            stockUserCookieDTO.setDuration(Integer.valueOf(dto.getDuration()));
        }
        stockUserCookieDTO.setCookie(tradeClient.getCurrentCookie());
        stockUserCookieDTO.setValidatekey(validateKey);
        updateCookie(stockUserCookieDTO);

    }

    private List<Header> getHeadList() {
        List<Header> headerList = new ArrayList<>();
        Header cookie1 = httpService.getHead("Content-Type", "application/x-www-form-urlencoded");
        Header cookie2 = httpService.getHead("X-Requested-With", "XMLHttpRequest");
        headerList.add(cookie1);
        headerList.add(cookie2);
        return headerList;
    }


    private Map<String, Object> getLoginParams(StockTradeUser stockTradeUser, StockTradeLoginDTO randomAndKey) {

        return getLoginParams(stockTradeUser, randomAndKey, null);
    }

    private Map<String, Object> getLoginParams(StockTradeUser stockTradeUser, StockTradeLoginDTO randomAndKey, StockTradeLoginDTO dto) {

        Map<String, Object> params = new HashMap<>();
        params.put("userId", stockTradeUser.getAccount());
        params.put("password", encodePassword(stockTradeUser.getPassword()));
        if (randomAndKey == null) {
            params.put("randNumber", dto.getRandNumber());
            params.put("identifyCode", dto.getIdentifyCode());
        } else {
            params.put("randNumber", randomAndKey.getRandNumber());
            params.put("identifyCode", randomAndKey.getIdentifyCode());
        }

        if (dto != null && StringUtils.isNotBlank(dto.getDuration())) {
            params.put("duration", dto.getPassword());
        } else {
            params.put("duration", "1800");
        }
        params.put("type", "Z");
        params.put("authCode", null);
        params.put("secInfo", null);
        return params;
    }


    public StockTradeLoginDTO getRandomAndIdentifyCode() {
        String aliAppCode = cronRefreshService.getAliAppCode();
        return getRandomAndIdentifyCode(aliAppCode);
    }


    public StockTradeLoginDTO getRandomAndIdentifyCode(String appcode) {
        StockTradeLoginDTO result = new StockTradeLoginDTO();
        //API产品路径
        String requestUrl = "https://codevirify.market.alicloudapi.com/icredit_ai_image/verify_code/v1";
        //阿里云APPCODE
        double random = Math.random();
        result.setRandNumber(String.valueOf(random));
        String url = "https://jywg.18.cn/Login/YZM?randNum=" + random;
        CloseableHttpClient httpClient = null;
        try {
            httpClient = HttpClients.createDefault();
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            // 装填参数
            if (false) {
                //启用BASE64编码方式进行识别
                //内容数据类型是BASE64编码
                String imgFile = url;
                String imgBase64 = "";
                try {
                    File file = new File(imgFile);
                    byte[] content = new byte[(int) file.length()];
                    FileInputStream finputstream = new FileInputStream(file);
                    finputstream.read(content);
                    finputstream.close();
                    imgBase64 = new String(encodeBase64(content));
                } catch (IOException e) {

                }
                params.add(new BasicNameValuePair("IMAGE", imgBase64));
                params.add(new BasicNameValuePair("IMAGE_TYPE", "0"));
            } else {
                //启用URL方式进行识别
                //内容数据类型是图像文件URL链接
                params.add(new BasicNameValuePair("IMAGE", url));
                params.add(new BasicNameValuePair("IMAGE_TYPE", "1"));
            }

            // 创建一个HttpGet实例
            HttpPost httpPost = new HttpPost(requestUrl);
            httpPost.addHeader("Authorization", "APPCODE " + appcode);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

            // 设置请求参数
            httpPost.setEntity(new UrlEncodedFormEntity(params, "utf-8"));

            // 发送GET请求
            HttpResponse execute = httpClient.execute(httpPost);


            // 获取结果
            HttpEntity entity = execute.getEntity();
            String identifyCode = EntityUtils.toString(entity, "utf-8");
            JSONObject jsonObject = JSONObject.parseObject(identifyCode);
            result.setIdentifyCode(jsonObject.getJSONObject("VERIFY_CODE_ENTITY").getString("VERIFY_CODE"));
        } catch (Exception e) {
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                }
            }
        }
        return result;
    }


    private String encodePassword(String password) {
        if (password.length() != 6) {
            return password;
        }
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDHdsyxT66pDG4p73yope7jxA92\nc0AT4qIJ/xtbBcHkFPK77upnsfDTJiVEuQDH+MiMeb+XhCLNKZGp0yaUU6GlxZdp\n+nLW8b7Kmijr3iepaDhcbVTsYBWchaWUXauj9Lrhz58/6AE/NF0aMolxIGpsi+ST\n2hSHPu3GSXMdhPCkWQIDAQAB";
        return RSAUtil.encodeWithPublicKey(password, publicKey);
    }

    private String getValidateKey(String content) {
        String key = "input id=\"em_validatekey\" type=\"hidden\" value=\"";
        int inputBegin = content.indexOf(key) + key.length();
        int inputEnd = content.indexOf("\" />", inputBegin);
        return content.substring(inputBegin, inputEnd);
    }

    public StockTradeUser baseInfo(String id) {
        StockTradeUser stockTradeUser = stockTradeUserMapper.selectByPrimaryKey(id);
        return stockTradeUser;
    }
}
