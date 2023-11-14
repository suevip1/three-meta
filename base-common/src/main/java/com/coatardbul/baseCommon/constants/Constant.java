package com.coatardbul.baseCommon.constants;

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


//    public static final String INVINCIBLE_TOKEN="拦截别人的token是不道德的，必须受到法律的制裁";

    public static final String INVINCIBLE_TOKEN="Intercepting other token is immoral, must must  must be punished by law";

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
