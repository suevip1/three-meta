package com.coatardbul.baseService.service;

import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseService.entity.bo.HttpConfigBo;
import com.coatardbul.baseService.entity.bo.HttpResponseInfo;
import com.google.common.net.HttpHeaders;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/25
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public abstract class HttpCommonService {

    public HttpRequestRetryHandler httpRequestRetryHandler;

    @Autowired
    CronRefreshService cronRefreshService;


    @Autowired
    ProxyIpService proxyIpService;


    public HttpResponseInfo executeHttpRequest(HttpConfigBo httpConfigBo, boolean isProxy) {
        HttpResponseInfo result = new HttpResponseInfo();

        CloseableHttpClient httpClient = httpConfigBo.getHttpClient();
        HttpRequestBase httpRequestBase = httpConfigBo.getHttpRequestBase();
        try {
            CloseableHttpResponse response = httpClient.execute(httpRequestBase);
            httpConfigBo.setCloseableHttpResponse(response);
            log.info("结果响应：" + httpRequestBase.toString() + "响应状态为:" + response.getStatusLine());
            //设置状态码
            result.setHttpStatus(response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //返回json格式
                String res = EntityUtils.toString(response.getEntity());
                result.setResponseStr(res);
            }
            //407 Proxy Authentication Required
//            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED) {
//                //返回json格式
//                throw new  ConnectTimeoutException();
//            }
        } catch (ConnectTimeoutException | HttpHostConnectException e) {
            if (isProxy) {
                //删除当前ip，重试
                proxyIpService.deleteByIp(httpConfigBo.getProxy().getHostName());
                log.error("删除代理ip：" + httpConfigBo.getProxy().getHostName() + " 端口：" + httpConfigBo.getProxy().getPort());
            }
//            result.setHttpStatus(HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED);
            log.error("httpclient超时异常" + e.getMessage());
        } catch (ClientProtocolException e) {
            log.error("ClientProtocolException" + e.getMessage());
        } catch (IOException e) {
            if (isProxy) {
                //删除当前ip，重试
                proxyIpService.deleteByIp(httpConfigBo.getProxy().getHostName());
                log.error("删除代理ip：" + httpConfigBo.getProxy().getHostName() + " 端口：" + httpConfigBo.getProxy().getPort());
            }
//            result.setHttpStatus(HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED);
            log.error("httpclient请求IO异常" + e.getMessage());
        } finally {
            closeStream(httpConfigBo);
        }
        return result;
    }


    /**
     * 通过重试可以解决
     */
    public void setHttpRequestRetryHandler() {
        httpRequestRetryHandler = new HttpRequestRetryHandler() {
            @Override
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                if (executionCount >= 5) {// 如果已经重试了5次，就放弃
                    return false;
                }
                if (exception instanceof NoHttpResponseException) {// 如果服务器丢掉了连接，那么就重试
                    return true;
                }
                if (exception instanceof SSLHandshakeException) {// 不要重试SSL握手异常
                    return false;
                }
                if (exception instanceof InterruptedIOException) {// 超时
                    return false;
                }
                if (exception instanceof UnknownHostException) {// 目标服务器不可达
                    return false;
                }
                if (exception instanceof ConnectTimeoutException) {// 连接被拒绝
                    return false;
                }
                if (exception instanceof SSLException) {// SSL握手异常
                    return false;
                }

                HttpClientContext clientContext = HttpClientContext.adapt(context);
                HttpRequest request = clientContext.getRequest();
                // 如果请求是幂等的，就再次尝试
                if (!(request instanceof HttpEntityEnclosingRequest)) {
                    return true;
                }
                return false;
            }
        };

    }


    abstract void closeStream(HttpConfigBo httpConfigBo);


    public void defaultCloseStream(HttpConfigBo httpConfigBo) {
        CloseableHttpClient httpClient = httpConfigBo.getHttpClient();
        HttpRequestBase httpRequestBase = httpConfigBo.getHttpRequestBase();
        CloseableHttpResponse response = httpConfigBo.getCloseableHttpResponse();
        try {
            // 释放资源
            if (httpClient != null) {
                httpClient.close();
            }
            if (httpRequestBase != null) {
                httpRequestBase.releaseConnection();
            }
            if (response != null) {
                HttpEntity entity = response.getEntity();
                EntityUtils.consumeQuietly(entity);
                EntityUtils.consume(entity);
                response.close();
            }
        } catch (IOException e) {
            log.error("httpclient关闭请求流异常");
        }
    }


    private HttpRequestBase setHttpGet(String url, List<Header> headerList, HttpConfigBo httpConfigBo) {
        HttpRequestBase httpGet = new HttpGet(url);
        setHeader(headerList, httpGet);
        httpConfigBo.setHttpRequestBase(httpGet);
        return httpGet;
    }

    private HttpRequestBase setHttpPost(String url, String jsonString, List<Header> headerList, HttpConfigBo httpConfigBo) {
        HttpRequestBase httpPost = setHttpPost(url, jsonString, headerList);
        httpConfigBo.setHttpRequestBase(httpPost);
        return httpPost;
    }


    /**
     * 对于Content-Type == application/x-www-form-urlencoded，需要设置请求头
     * @param url
     * @param jsonString
     * @param headerList
     * @return
     */
    public HttpPost setHttpPost(String url, String jsonString, List<Header> headerList) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity entity = new StringEntity(jsonString, "UTF-8");
        // post请求是将参数放在请求体里面传过去的;这里将entity放入post请求体中
        httpPost.setEntity(entity);
        if (headerList != null && headerList.size() > 0) {
            List<Header> collect = headerList.stream().filter(item -> item.getName().equals("Content-Type")).collect(Collectors.toList());
            if (collect.size() == 0) {
                httpPost.addHeader("Content-Type", "application/json;charset=utf8");
            }
        }
        setHeader(headerList, httpPost);
        return httpPost;
    }




    public void setPostMapEntity(HttpEntityEnclosingRequestBase request, Map<String, Object> params) {
        if (params != null) {
            List<BasicNameValuePair> parameters = params.entrySet().stream().map(entry ->
                    new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue()))
            ).collect(Collectors.toList());
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, Consts.UTF_8);
            request.setEntity(entity);
        }

    }


    private void setHeader(List<Header> headerList, HttpRequestBase httpRequestBase) {
        if (headerList != null && headerList.size() > 0) {
            for (Header headerTemp : headerList) {
                httpRequestBase.addHeader(headerTemp);
            }
        }
        httpRequestBase.addHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/107.0.0.0 Safari/537.36");
//        httpRequestBase.addHeader(HttpHeaders.CONNECTION, "close");
    }


    public HttpConfigBo getHttpConfig(boolean isProxy) {
        HttpConfigBo httpConfigBo = new HttpConfigBo();
        //请求配置，配置代理，超时时间等
        setRequestConfig(httpConfigBo, isProxy);
        //配置客户端
        setCloseableHttpClient(httpConfigBo, isProxy);

        return httpConfigBo;

    }

    abstract void setCloseableHttpClient(HttpConfigBo httpConfigBo, boolean isProxy);

    public void setRequestConfig(HttpConfigBo httpConfigBo, boolean isProxy) {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();
        Integer sockTimeout = cronRefreshConfigBo.getSockTimeout();
        RequestConfig defaultRequestConfig = null;
        if (isProxy) {
            HttpHost proxy = proxyIpService.getNewProxyHttpHost();
            httpConfigBo.setProxy(proxy);
            defaultRequestConfig = RequestConfig.custom().setConnectTimeout(sockTimeout).setConnectionRequestTimeout(sockTimeout).setSocketTimeout(sockTimeout).setProxy(proxy).build();
        } else {
            defaultRequestConfig = RequestConfig.custom().setConnectTimeout(sockTimeout).setConnectionRequestTimeout(sockTimeout).setSocketTimeout(sockTimeout).build();
        }
        httpConfigBo.setDefaultRequestConfig(defaultRequestConfig);
    }


    /**
     * 有代理，使用代理的配置，没有代理，使用默认配置
     *
     * @param proxy
     * @param isProxy
     * @return
     */
    public CloseableHttpClient getProxyHttpClient(HttpHost proxy, boolean isProxy) {
        Integer sockTimeout = cronRefreshService.getSockTimeout();
        CloseableHttpClient httpClient = null;
        if (isProxy) {
            RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(sockTimeout)
                    .setConnectionRequestTimeout(sockTimeout)
                    .setSocketTimeout(sockTimeout)
                    .setProxy(proxy).build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setRetryHandler(httpRequestRetryHandler).build();
        } else {
            // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
            RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(sockTimeout)
                    .setConnectionRequestTimeout(sockTimeout)
                    .setSocketTimeout(sockTimeout)
                    .build();
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setRetryHandler(httpRequestRetryHandler).build();
        }
        return httpClient;
    }

    public CloseableHttpClient getProxyHttpClient(CookieStore cookieStore) {
        Integer sockTimeout = cronRefreshService.getSockTimeout();
        CloseableHttpClient httpClient = null;

        // 获得Http客户端(可以理解为:你得先有一个浏览器;注意:实际上HttpClient与浏览器是不一样的)
        RequestConfig defaultRequestConfig = RequestConfig.custom().setConnectTimeout(sockTimeout)
                .setConnectionRequestTimeout(sockTimeout)
                .setSocketTimeout(sockTimeout)
                .build();
        httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setDefaultCookieStore(cookieStore).setRetryHandler(httpRequestRetryHandler).build();

        return httpClient;
    }


    /**
     * 获取头信息
     *
     * @param param
     * @param value
     * @return
     */
    public Header getHead(String param, String value) {
        return new BasicHeader(param, value);
    }


    public String doGet(String url) throws ConnectTimeoutException {
        return doGet(url, null, false);
    }

    public String doGet(String url, List<Header> headerList, boolean isProxy) throws ConnectTimeoutException {
        //创建HttpClient对象
        HttpConfigBo httpConfigBo = getHttpConfig(isProxy);
        //设置请求头
        setHttpGet(url, headerList, httpConfigBo);
        //执行请求
        HttpResponseInfo httpResponseInfo = executeHttpRequest(httpConfigBo, isProxy);

        if (HttpStatus.SC_OK == httpResponseInfo.getHttpStatus()) {
            return httpResponseInfo.getResponseStr();
        } else {
            return null;
        }
    }

    /**
     * 传入的为对象json
     *
     * @param url
     * @param jsonString
     * @param headerList
     * @return
     */
    public String doPost(String url, String jsonString, List<Header> headerList) throws ConnectTimeoutException {
        return doPost(url, jsonString, headerList, true);
    }

    public String doPost(String url, String jsonString, List<Header> headerList, boolean isProxy) throws ConnectTimeoutException {
        //创建HttpClient对象
        HttpConfigBo httpConfigBo = getHttpConfig(isProxy);
        //设置请求头
        setHttpPost(url, jsonString, headerList, httpConfigBo);

        HttpResponseInfo httpResponseInfo = executeHttpRequest(httpConfigBo, isProxy);
        //执行请求
        if (HttpStatus.SC_OK == httpResponseInfo.getHttpStatus()) {
            return httpResponseInfo.getResponseStr();
        } else {
            return null;
        }
        //默认json，可以覆盖

    }
}
