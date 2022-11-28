
package com.coatardbul.river.feign.fallback;

import com.coatardbul.river.feign.BaseServerFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: suxiaolei
 * @date: 2019/7/23
 */
@Slf4j
@Component
public class BaseServerFeignFallback implements FallbackFactory<BaseServerFeign> {
    /**
     * Returns an instance of the fallback appropriate for the given cause
     *
     *
     */
    @Override
    public BaseServerFeign create(Throwable cause) {
       return new BaseServerFeign() {
           @Override
           public String getSnowflakeId() {
               log.error("调用失败", cause);
               return null;
           }
       };
    }
}
