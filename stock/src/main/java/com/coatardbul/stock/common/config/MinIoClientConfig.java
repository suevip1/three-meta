package com.coatardbul.stock.common.config;

import org.springframework.beans.factory.annotation.Value;
import io.minio.MinioClient;
import io.swagger.annotations.ApiModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ApiModel(value = "minio配置")
public class MinIoClientConfig {
    @Value("${minIo.endpoint}")
    private String url;
    @Value("${minIo.access-key}")
    private String accessKey;
    @Value("${minIo.secret-key}")
    private String secretKey;
    @Bean
    public MinioClient minioClient(){

        return MinioClient.builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }
}
