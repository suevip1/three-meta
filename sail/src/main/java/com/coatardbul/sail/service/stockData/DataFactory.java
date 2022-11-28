package com.coatardbul.sail.service.stockData;

import com.coatardbul.baseCommon.constants.DataSourceEnum;
import com.coatardbul.baseService.service.DataServiceBridge;
import com.coatardbul.sail.service.StockCronRefreshService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class DataFactory {

    @Resource
    XinlangService xinlangService;
    @Resource
    DongFangService dongFangService;
    @Resource
    StockCronRefreshService stockCronRefreshService;

    public DataServiceBridge build() {
        try {
            String dataSource = stockCronRefreshService.getDataSource();
            if (DataSourceEnum.XIN_LANG.getSign().equals(dataSource)) {
                return xinlangService;
            }else if (DataSourceEnum.DONG_FANG.getSign().equals(dataSource)) {
                return dongFangService;
            }else {
                return xinlangService;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return xinlangService;
        }

    }
}
