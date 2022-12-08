
package com.coatardbul.sail.feign.fallback;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.dto.StockTemplateQueryDTO;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.sail.feign.RiverServerFeign;
import com.coatardbul.sail.feign.StockServerFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: suxiaolei
 * @date: 2019/7/23
 */
@Slf4j
@Component
public class StockServerFeignFallback implements FallbackFactory<StockServerFeign> {


    @Override
    public StockServerFeign create(Throwable throwable) {
        return new StockServerFeign() {
            @Override
            public CommonResult<String> add(StockTradeBuyTask dto) {
                log.error("调用失败", throwable);
                return null;
            }
        };
    }
}
