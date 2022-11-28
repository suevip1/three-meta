package com.coatardbul.river.common.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Slf4j
public class InitData {

    @PostConstruct
    public void InitCache() {
        log.info("项目启动时开始加载数据：");
        log.info("项目启动时数据加载线束");
    }

}
