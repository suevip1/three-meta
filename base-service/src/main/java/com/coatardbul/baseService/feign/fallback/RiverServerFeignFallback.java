
package com.coatardbul.baseService.feign.fallback;


import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.model.entity.DictInfo;
import com.coatardbul.baseService.entity.feign.CalendarDateDTO;
import com.coatardbul.baseService.entity.feign.CalendarSpecialDTO;
import com.coatardbul.baseService.entity.feign.StockTemplateDto;
import com.coatardbul.baseService.entity.feign.StockTemplateQueryDTO;
import com.coatardbul.baseService.entity.feign.StockTimeInterval;

import com.coatardbul.baseService.feign.RiverServerFeign;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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

            /**
             * 间隔数据
             *
             * @param dto
             * @return
             */
            @Override
            public CommonResult<List<String>> getTimeIntervalList(StockTimeInterval dto) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult<List<String>> getDate(CalendarDateDTO dto) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult<StockTemplateDto> findOne(StockTemplateDto dto) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult<String> getSpecialDay(CalendarSpecialDTO dto) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult<Boolean> verifyUserValid(String token) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult logLoginInfo(HttpServletRequest request) {
                log.error("调用失败", throwable);
                return null;
            }

            @Override
            public CommonResult<String> getCurrUserName() {
                log.error("调用失败", throwable);
                return null;            }

            @Override
            public CommonResult<List<DictInfo>> getInfoByType(Map map) {
                log.error("调用失败", throwable);
                return null;
            }
        };
    }
}
