package com.coatardbul.baseService.service;


import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseService.entity.bo.HttpConfigBo;
import com.coatardbul.baseService.utils.HttpClientConnectionMonitorThread;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.List;

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
        connectionManager.setDefaultMaxPerRoute(2);

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
