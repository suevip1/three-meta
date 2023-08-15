package com.coatardbul.baseService.service;


import com.coatardbul.baseService.entity.bo.HttpConfigBo;
import com.coatardbul.baseService.utils.HttpClientConnectionMonitorThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class HttpPoolService extends HttpCommonService {

    private PoolingHttpClientConnectionManager connectionManager;

    private HttpClientConnectionMonitorThread thread;



    @PostConstruct
    public void init() {//bean初始化
        if (connectionManager == null ) {
            reInit();
        }
    }

    public void reInit(){
        connectionManager = new PoolingHttpClientConnectionManager();
        // 整个连接池最大连接数
        connectionManager.setMaxTotal(20);
        // 每路由最大连接数，默认值是2
        connectionManager.setDefaultMaxPerRoute(5);

//            connectionManager.setValidateAfterInactivity(3000);
        //重试
        setHttpRequestRetryHandler();
        /** 管理 http连接池 */
        thread = new HttpClientConnectionMonitorThread(connectionManager);
    }

    public void closeConnection() {
        connectionManager.shutdown();
    }

//    private void dfd(){
//        ConnectionKeepAliveStrategy myStrategy = new ConnectionKeepAliveStrategy() {
//
//            @Override
//            public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
//                // Honor 'keep-alive' header
//                HeaderElementIterator it = new BasicHeaderElementIterator(
//                        response.headerIterator(HTTP.CONN_KEEP_ALIVE));
//                while (it.hasNext()) {
//                    HeaderElement he = it.nextElement();
//                    String param = he.getName();
//                    String value = he.getValue();
//                    if (value != null && param.equalsIgnoreCase("timeout")) {
//                        try {
//                            return Long.parseLong(value) * 1000;
//                        } catch(NumberFormatException ignore) {
//                        }
//                    }
//                }
//                HttpHost target = (HttpHost) context.getAttribute(
//                        HttpClientContext.HTTP_TARGET_HOST);
//                if ("www.naughty-server.com".equalsIgnoreCase(target.getHostName())) {
//                    // Keep alive for 5 seconds only
//                    return 5 * 1000;
//                } else {
//                    // otherwise keep alive for 30 seconds
//                    return 30 * 1000;
//                }
//            }
//
//        };
//    }


    @Override
    void closeStream(HttpConfigBo httpConfigBo) {
        defaultCloseStream(httpConfigBo);
    }

    @Override
    void setCloseableHttpClient(HttpConfigBo httpConfigBo, boolean isProxy) {
        RequestConfig newRequestConfig = httpConfigBo.getDefaultRequestConfig();
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).setDefaultRequestConfig(newRequestConfig).setConnectionManagerShared(true).setRetryHandler(httpRequestRetryHandler).build();
        httpConfigBo.setHttpClient(httpClient);
    }
}
