//package com.coatardbul.stock.common.config.powerJob;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import tech.powerjob.worker.PowerJobSpringWorker;
//import tech.powerjob.worker.common.PowerJobWorkerConfig;
//import tech.powerjob.worker.common.constants.StoreStrategy;
//
//import java.util.List;
//
///**
// * <p>
// * Note:
// * <p>
// * Date: 2023/11/14
// *
// * @author Su Xiaolei
// */
//@Configuration
//public class PowerJobWorkerConfiguration {
//    @Value("${powerjob.worker.app-name}")
//    private String appName;
//    @Value("${powerjob.worker.port}")
//    private int port;
//
//    @Value("${powerjob.worker.server-address}")
//    private List<String> serverAddress;
//    @Bean
//    public PowerJobSpringWorker initPowerJobWorker() throws Exception {
//
//        // 1. 创建配置文件
//        PowerJobWorkerConfig config = new PowerJobWorkerConfig();
//        config.setAppName(appName);
//        config.setPort(port);
//        config.setServerAddress(serverAddress);
////        config.setProtocol();
////        config.setMaxResultLength();
////        config.setUserContext();
//        config.setStoreStrategy(StoreStrategy.DISK);
////        config.setAllowLazyConnectServer();
////        config.setMaxAppendedWfContextLength();
////        config.setSystemMetricsCollector();
////        config.setProcessorFactoryList();
////        config.setTag();
////        config.setMaxLightweightTaskNum();
////        config.setMaxHeavyweightTaskNum();
////        config.setHealthReportInterval();
//        ;
//        // 如果没有大型 Map/MapReduce 的需求，建议使用内存来加速计算
//
//        // 2. 创建 Worker 对象，设置配置文件（注意 Spring 用户需要使用 PowerJobSpringWorker，而不是 PowerJobWorker）
//        PowerJobSpringWorker worker = new PowerJobSpringWorker(config);
//        return worker;
//    }
//}
