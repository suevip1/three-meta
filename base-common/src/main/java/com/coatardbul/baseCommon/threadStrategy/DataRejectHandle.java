package com.coatardbul.baseCommon.threadStrategy;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 * Note:拒绝策略
 * <p>
 * Date: 2022/11/15
 *
 * @author Su Xiaolei
 */
public class DataRejectHandle  implements RejectedExecutionHandler {
    public DataRejectHandle() {
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (!executor.isShutdown()) {
            executor.getQueue().clear();
            executor.execute(r);
        }
    }
}
