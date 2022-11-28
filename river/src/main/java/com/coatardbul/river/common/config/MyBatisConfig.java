package com.coatardbul.river.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan("com.coatardbul.river.mapper")
public class MyBatisConfig {
}
