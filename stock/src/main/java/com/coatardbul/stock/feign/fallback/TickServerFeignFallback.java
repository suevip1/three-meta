
package com.coatardbul.stock.feign.fallback;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.stock.feign.TickServerFeign;
import com.coatardbul.stock.model.dto.StockCronRefreshDTO;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: suxiaolei
 * @date: 2019/7/23
 */
@Slf4j
@Component
public class TickServerFeignFallback implements FallbackFactory<TickServerFeign> {


    @Override
    public TickServerFeign create(Throwable throwable) {
        return new TickServerFeign() {

            @Override
            public CommonResult refreshTickInfo(StockCronRefreshDTO dto) {
                log.error(throwable.getMessage(), throwable);
                return null;
            }
        };
    }
}
