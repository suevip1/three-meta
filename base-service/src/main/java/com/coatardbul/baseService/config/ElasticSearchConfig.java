package com.coatardbul.baseService.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;


@Data
@Slf4j
@Configuration
public class ElasticSearchConfig implements EnvironmentAware {

    private Environment environment;
    private String ip;
    private Integer port;

    private String username;

    private String password;

    @Override
    public void setEnvironment(Environment environment) {
        this.ip = environment.getProperty("elasticsearch.ip");
        this.port = Integer.valueOf(environment.getProperty("elasticsearch.port"));
        this.username= environment.getProperty("elasticsearch.username");
        this.password=environment.getProperty("elasticsearch.password");
        this.environment = environment;
    }

    private RestHighLevelClient r;

    @Bean
    public RestHighLevelClient esRestClient() {
        RestClientBuilder builder = RestClient.builder(
                new HttpHost(ip, port, "http"));
        //开始设置用户名和密码
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        builder.setHttpClientConfigCallback(f -> f.setDefaultCredentialsProvider(credentialsProvider));
        RestHighLevelClient client = new RestHighLevelClient(
                builder);
        this.r = client;
        return client;
    }
}
