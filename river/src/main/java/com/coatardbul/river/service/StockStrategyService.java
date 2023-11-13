package com.coatardbul.river.service;

import com.coatardbul.baseCommon.model.bo.StrategyQueryBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockStrategyService extends StockStrategyCommonService {

    @Autowired
    RiverServerFeign riverServerFeign;


    /**
     * 将请求中的dto转换成策略对象
     *
     * @param dto                  抽象请求数据
     * @param defaultStrategyQuery 策略对象
     */
     @Override
    public void setRequestInfo(StockStrategyQueryDTO dto, StrategyQueryBO defaultStrategyQuery) {

    }




}
