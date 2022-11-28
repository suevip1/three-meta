
package com.coatardbul.sail.feign.fallback;


import com.coatardbul.sail.model.feign.StockTemplateQueryDTO;
import com.coatardbul.sail.common.api.CommonResult;
import com.coatardbul.sail.feign.RiverServerFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: suxiaolei
 * @date: 2019/7/23
 */
@Slf4j
@Component
public class RiverServerFeignFallback implements FallbackFactory<RiverServerFeign> {


    @Override
    public RiverServerFeign create(Throwable throwable) {
        return new RiverServerFeign() {
            @Override
            public CommonResult<String> getQuery(StockTemplateQueryDTO dto) {
                log.error("调用失败", throwable);
                return null;
            }



        };
    }
}
