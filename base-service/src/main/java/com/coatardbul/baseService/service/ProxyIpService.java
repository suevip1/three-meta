package com.coatardbul.baseService.service;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.model.entity.ProxyIp;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.dto.ProxyIpQueryDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/3/14
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class ProxyIpService {

    private static final String PROXY_IP = "proxyIp";

    @Autowired
    HttpService httpService;
    @Autowired
    RedisTemplate redisTemplate;



    public void addIpProcess(ProxyIpQueryDTO dto) throws ConnectTimeoutException {
        StringBuilder url = new StringBuilder("https://proxyapi.horocn.com/api/v2/proxies?");
        url.append("order_id=").append(dto.getOrderId());
        url.append("&num=").append(dto.getNum());
        url.append("&format=").append(dto.getFormat());
        url.append("&line_separator=").append(dto.getLineSeparator());
        url.append("&can_repeat=").append(dto.getCanRepeat());
        url.append("&user_token=").append(dto.getUserToken());
        String response = httpService.doGet(url.toString());
        Integer code = (Integer) JSONObject.parseObject(response).get("code");
        if (0 == code) {
            JSONArray data = JSONObject.parseObject(response).getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject jsonObject = data.getJSONObject(i);
                ProxyIp proxyIp = new ProxyIp();
                proxyIp.setIp((String) jsonObject.get("host"));
                proxyIp.setPort((String) jsonObject.get("port"));
                proxyIp.setCountry((String) jsonObject.get("country_cn"));
                proxyIp.setProvince((String) jsonObject.get("province_cn"));
                proxyIp.setCity((String) jsonObject.get("city_cn"));
                proxyIp.setCreateTime(new Date());
                String key = PROXY_IP + "_" + proxyIp.getIp() + "_" + proxyIp.getPort();
                redisTemplate.opsForValue().set(key, JsonUtil.toJson(proxyIp), 1, TimeUnit.MINUTES);
            }
        }
    }


    /**
     * 获取最新的代理ip，端口
     *
     * @return
     */
    public HttpHost getNewProxyHttpHost() {
        Set keys = redisTemplate.keys(PROXY_IP + "*");
        if (keys.size() > 0) {
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String key = codeKey.toString();
                    String[] s = key.split("_");
                    return new HttpHost(s[1], Integer.valueOf(s[2]));
                }
            }
        }
        return null;
    }

    public List<ProxyIp> getAllIps() {
        List<ProxyIp> result = new ArrayList<ProxyIp>();
        Set keys = redisTemplate.keys(PROXY_IP + "*");
        if (keys.size() > 0) {
            for (Object codeKey : keys) {
                if (codeKey instanceof String) {
                    String key = codeKey.toString();
                    String jsonStr = (String) redisTemplate.opsForValue().get(key);
                    ProxyIp proxyIp = JsonUtil.readToValue(jsonStr, ProxyIp.class);
                    result.add(proxyIp);
                }
            }
        }
        return result;
    }

    public void deleteByIp(String ip) {
        Set keys = redisTemplate.keys(PROXY_IP+"_"+ip + "*");
        if (keys.size() > 0) {
            redisTemplate.delete(keys);
        }
    }
}
