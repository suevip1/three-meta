package com.coatardbul.stock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "com.coatardbul.baseService.feign")
@SpringBootApplication(scanBasePackages = {"com.coatardbul.stock","com.coatardbul.baseCommon.exception","com.coatardbul.baseService"})
public class StockApplication {

    public static void main(String[] args) {
        SpringApplication.run(StockApplication.class, args);
    }

}
