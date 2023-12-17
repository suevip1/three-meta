package com.coatardbul.stock.service.base;

import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.HttpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>
 * Note:将curl 转换成http 相关的参数
 * <p>
 * Date: 2023/12/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class CurlService {

    @Autowired
    HttpService httpService;

    /**
     * 解析curl，模拟postman发送请求
     * @param curlCmd
     * @return
     * @throws ConnectTimeoutException
     */
    public Map parseCurl(String curlCmd) throws ConnectTimeoutException {
        String[] curlSplit = curlCmd.split("\n");
        //url 带参数
        String url = curlSplit[0].split("'")[1].trim();
        Map<String, String> paramurlMap = new HashMap<String, String>();
        int queryIndex = url.indexOf("?");
        //请求里面的参数
        if(queryIndex>-1){
            String paramUrl = url.substring(queryIndex + 1, url.length());
            if (StringUtils.isNotBlank(paramUrl)) {
                String[] paramStrs = paramUrl.split("&");
                for (String paramStr : paramStrs) {
                    String[] split1 = paramStr.trim().split("=");
                    paramurlMap.put(split1[0], split1[1]);
                }
            }
        }
        //请求头
        List<String> headList = Stream.of(curlSplit).filter(item -> item.trim().startsWith("-H")).collect(Collectors.toList());
        Map<String, String> headMap = new HashMap<String, String>();
        if (headList != null && headList.size() > 0) {
            for (String headLineStr : headList) {
                String headStr = headLineStr.split("'")[1];
                int headIndex = headStr.indexOf(":");
                headMap.put(headStr.substring(0,headIndex), headStr.substring(headIndex+1,headStr.length()).trim());
            }
        }
        List<Header> headerList = new ArrayList<>();
        for (Map.Entry<String, String> tempMap : headMap.entrySet()) {
            Header header = httpService.getHead(tempMap.getKey(), tempMap.getValue());
            headerList.add(header);
        }

        //请求对象
        List<String> bodyList = Stream.of(curlSplit).filter(item -> item.trim().startsWith("--data")).collect(Collectors.toList());
        String result="";
        String bodyResultStr = "";
        if (bodyList != null && bodyList.size() > 0) {
            bodyResultStr = bodyList.get(0).split("'")[1];
            Map<String, Object> bodyMap = new HashMap<String, Object>();
            bodyMap = JsonUtil.readToValue(bodyResultStr, Map.class);
             result = httpService.doPost(url, bodyResultStr, headerList);
        }else {
            result = httpService.doGet(url, headerList, true);
        }
        Map resultMap=new HashMap();
        if(StringUtils.isNotBlank(result)){
            try {
                resultMap= JsonUtil.readToValue(result,Map.class);
            }catch (Exception e){
                //todo 非json类型
                resultMap.put("result",result);
            }
        }

        return resultMap;
    }
}
