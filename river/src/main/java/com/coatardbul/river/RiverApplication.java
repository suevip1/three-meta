package com.coatardbul.river;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@MapperScan("com.coatardbul.river.mapper")
@SpringBootApplication(scanBasePackages = {"com.coatardbul.river", "com.coatardbul.baseService"})
@EnableFeignClients(basePackages = "com.coatardbul.river.feign")
public class RiverApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiverApplication.class, args);
    }

}
