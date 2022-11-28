package com.coatardbul.sail.common.constans;

import com.coatardbul.baseCommon.threadStrategy.DataRejectHandle;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/8
 *
 * @author Su Xiaolei
 */
public class Constant {


    /**
     * 股票更新线程池
     */
    public static ThreadPoolExecutor dataThreadPool =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors()*5,
                    Runtime.getRuntime().availableProcessors()*10,
                    1,
                    TimeUnit.MINUTES,
                    new ArrayBlockingQueue<Runnable>(100),
                    new ThreadFactoryBuilder().setNameFormat("ab-pool-%d").build(),
                    new DataRejectHandle()
            );

    /**
     * 股票更新线程池
     */
    public static ThreadPoolExecutor strategyThreadPool =
            new ThreadPoolExecutor(
                    Runtime.getRuntime().availableProcessors()*5,
                    Runtime.getRuntime().availableProcessors()*10,
                    1,
                    TimeUnit.MINUTES,
                    new ArrayBlockingQueue<Runnable>(100),
                    new ThreadFactoryBuilder().setNameFormat("strategy-pool-%d").build(),
                    new DataRejectHandle()
            );

}
