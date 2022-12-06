package com.coatardbul.baseService.client;

import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseService.service.HttpCommonService;
import com.coatardbul.baseService.service.HttpService;
import lombok.Data;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.stereotype.Component;


import javax.annotation.Resource;
import java.io.IOException;

import java.util.stream.Collectors;

@Component
public class TradeClient {

    @Resource
    HttpService httpCommonService;

    private final ThreadLocal<ClientWrapper> threadLocal = new ThreadLocal<>();



    public void openSession() {


        ClientWrapper wrapper = new ClientWrapper();
        wrapper.cookieStore = new BasicCookieStore();
        wrapper.httpClient = httpCommonService.getProxyHttpClient(wrapper.cookieStore);
        threadLocal.set(wrapper);
    }

    public ClientWrapper getCurrent() {
        ClientWrapper clientWrapper = threadLocal.get();
        return clientWrapper;
    }
    public String getCurrentCookie() {
        ClientWrapper clientWrapper = threadLocal.get();
        assertOpened(clientWrapper);
        return clientWrapper.cookieStore.getCookies().stream().map(cookie -> cookie.getName() + "=" + cookie.getValue()).collect(Collectors.joining("; "));
    }

    public void destoryCurrentSession() {
        ClientWrapper clientWrapper = threadLocal.get();
        threadLocal.remove();
        assertOpened(clientWrapper);
        try {
            clientWrapper.httpClient.close();
        } catch (IOException e) {
            // ignore
        }
    }

    private void assertOpened(ClientWrapper clientWrapper) {
        if (clientWrapper == null) {
            throw new BusinessException("please call open session first");
        }
    }

    @Data
    public static class ClientWrapper {
        private CloseableHttpClient httpClient;
        private CookieStore cookieStore;
    }

}
