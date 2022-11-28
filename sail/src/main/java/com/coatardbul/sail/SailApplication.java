package com.coatardbul.sail;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableSwagger2
@MapperScan("com.coatardbul.sail.mapper")
@EnableFeignClients(basePackages = "com.coatardbul.sail.feign")
@SpringBootApplication(scanBasePackages = {"com.coatardbul.sail","com.coatardbul.baseService"})
public class SailApplication {

    public static void main(String[] args) {
        SpringApplication.run(SailApplication.class, args);
    }

}
