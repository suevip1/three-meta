package com.coatardbul.baseService.service;


import com.coatardbul.baseService.entity.bo.HttpConfigBo;
import com.coatardbul.baseService.utils.HttpClientConnectionMonitorThread;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
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
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
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

/**
 * 目前暂时弃用,请使用
 *
 * @see HttpPoolService
 */
@Slf4j
@Service
public class HttpService extends HttpCommonService {


    @PostConstruct
    private void init() {//bean初始化
        setHttpRequestRetryHandler();

    }


    @Override
    void closeStream(HttpConfigBo httpConfigBo) {
        defaultCloseStream(httpConfigBo);
    }

    @Override
    void setCloseableHttpClient(HttpConfigBo httpConfigBo, boolean isProxy) {
        RequestConfig newRequestConfig = httpConfigBo.getDefaultRequestConfig();
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(newRequestConfig).setRetryHandler(httpRequestRetryHandler).build();
        httpConfigBo.setHttpClient(httpClient);
    }

}
