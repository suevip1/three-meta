//package com.coatardbul.base_server.common.util;
//
//import cn.hutool.core.lang.Snowflake;
//import cn.hutool.core.net.NetUtil;
//import cn.hutool.core.util.IdUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//
///**
// * 雪花算法获取分布式ID
// *
// * @author zrj
// * @date 2021/2/15
// * @since V1.0
// **/
//@Slf4j
//@Component
//public class IdGeneratorSnowflake {
//
//    /**
//     * 机房ID
//     */
//    private long workerId = 0;
//    /**
//     * 机器ID
//     */
//    private long datacenterId = 1;
//
//    private Snowflake snowflake = IdUtil.createSnowflake( workerId, datacenterId );
//
//    @PostConstruct
//    public void init() {
//        try {
//            workerId = NetUtil.ipv4ToLong( NetUtil.getLocalhostStr() );
//            log.info( "当前机器的workerId：{}", workerId );
//        } catch (Exception e) {
//            log.warn( "当前机器workerId获取失败", e );
//            workerId = NetUtil.getLocalhostStr().hashCode();
//        }
//    }
//
//    /**
//     * 无参获取通过默认机房机器号获取id
//     *
//     * @return
//     */
//    public synchronized long snowflakeId() {
//        return snowflake.nextId();
//    }
//
//    /**
//     * 有参根据参数获取ID
//     *
//     * @param workerId     机房ID
//     * @param datacenterId 机器ID
//     * @return
//     */
//    public synchronized long snowflakeId(long workerId, long datacenterId) {
//        Snowflake snowflake = IdUtil.createSnowflake( workerId, datacenterId );
//        return snowflake.nextId();
//    }
//
//
//}
