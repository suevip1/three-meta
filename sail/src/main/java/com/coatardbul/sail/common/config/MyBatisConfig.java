package com.coatardbul.sail.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 *
 */
@Configuration
@MapperScan("com.coatardbul.sail.mapper")
public class MyBatisConfig {
}
