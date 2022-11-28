package com.coatardbul.stock.service.statistic.trade;

import com.coatardbul.stock.mapper.StockTradeUrlMapper;
import com.coatardbul.stock.model.dto.StockUserCookieDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/3
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeUserService {


    @Autowired
    StockTradeBaseService stockTradeBaseService;


    @Autowired
    StockTradeUrlMapper stockTradeUrlMapper;

    public void updateCookie(StockUserCookieDTO dto) {
        if(StringUtils.isNotBlank(dto.getCookie())){
            stockTradeBaseService.updateCookie(dto);
        }
        stockTradeUrlMapper.updateValidateKey(dto.getValidatekey());
    }
}
