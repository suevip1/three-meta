package com.coatardbul.stock.task.powerJob;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.annotation.PowerJobHandler;
import tech.powerjob.worker.core.processor.TaskContext;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/14
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class HelloDemo {
    @PowerJobHandler(name = "testEmptyReturn")
    public String testEmptyReturn(TaskContext context) {
        log.info("你以为你会爱上谁");
        return "响应结果，正常返回视为执行成功，抛出异常视为执行失败";
    }
}
