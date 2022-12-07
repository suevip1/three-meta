package com.coatardbul.baseService.utils;

import org.apache.http.conn.HttpClientConnectionManager;

import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:解决close-wait ，闲置的client 没有正常关系，通过守护现成，将http池中的连接清空
 * <p>
 * Date: 2022/11/23
 *
 * @author Su Xiaolei
 */
public class HttpClientConnectionMonitorThread extends Thread {
    private final HttpClientConnectionManager connManager;
    private volatile boolean shutdown = false;

    public HttpClientConnectionMonitorThread(HttpClientConnectionManager connManager) {
        super();
        this.setName("http-connection-monitor");
        this.setDaemon(true);
        this.connManager = connManager;
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    // 等待 秒
                    wait(2000);
                    // 关闭过期的链接
                    connManager.closeExpiredConnections();
                    // 选择关闭 空闲30秒的链接
                    connManager.closeIdleConnections(30, TimeUnit.SECONDS);
                }
            }
        } catch (InterruptedException ex) {
            //ignore ex
        }
    }


}
