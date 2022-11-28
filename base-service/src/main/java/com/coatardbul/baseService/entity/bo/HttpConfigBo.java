package com.coatardbul.baseService.entity.bo;

import lombok.Data;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/23
 *
 * @author Su Xiaolei
 */
@Data
public class HttpConfigBo {

    private HttpHost proxy;

    private RequestConfig defaultRequestConfig;

    private CloseableHttpClient httpClient;


    private HttpRequestBase httpRequestBase;


    private CloseableHttpResponse closeableHttpResponse;


}
