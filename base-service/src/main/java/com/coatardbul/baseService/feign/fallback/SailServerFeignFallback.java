
package com.coatardbul.baseService.feign.fallback;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseService.entity.dto.StockCronRefreshDTO;
import com.coatardbul.baseService.feign.SailServerFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: suxiaolei
 * @date: 2019/7/23
 */
@Slf4j
@Component
public class SailServerFeignFallback implements FallbackFactory<SailServerFeign> {


    @Override
    public SailServerFeign create(Throwable throwable) {
        return new SailServerFeign() {
            @Override
            public CommonResult refreshStockInfo(StockCronRefreshDTO dto) {
                log.error(throwable.getMessage(), throwable);
                return null;
            }

            @Override
            public CommonResult refreshHisStockInfo(StockCronRefreshDTO dto) {
                log.error(throwable.getMessage(), throwable);
                return null;
            }

            @Override
            public CommonResult refreshStockMinuterInfo(StockCronRefreshDTO dto) {
                log.error(throwable.getMessage(), throwable);
                return null;
            }

            @Override
            public CommonResult getThreadPoolConfig() {
                log.error(throwable.getMessage(), throwable);

                return null;
            }
        };
    }
}
