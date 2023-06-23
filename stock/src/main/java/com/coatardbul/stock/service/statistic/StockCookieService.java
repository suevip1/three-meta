package com.coatardbul.stock.service.statistic;

import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseService.feign.BaseServerFeign;
import com.coatardbul.baseService.feign.SailServerFeign;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.model.dto.StockCookieDTO;
import com.coatardbul.stock.model.entity.AccountBase;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.base.StockStrategyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/1/15
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockCookieService {


    @Autowired
    StockUserBaseService stockUserBaseService;
    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    SailServerFeign sailServerFeign;


    /**
     * 同花顺登陆账号cookie
     *
     * @param dto
     */
    public void simpleModify(StockCookieDTO dto) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);

        AccountBase accountBase = new AccountBase();
        accountBase.setUserId(userName);
        accountBase.setCookie(dto.getCookie());
        accountBase.setTradeType(dto.getTradeType());
        accountBaseMapper.updateByUserIdAndTradeTypeSelective(accountBase);
        //更新其他服务启的cookie
        if(CookieTypeEnum.TONG_HUA_SHUN.getType().equals(dto.getTradeType())){
            stockStrategyService.setCookieValue(dto.getCookie());
            //调用sail，刷新sailcookie
            for (int i = 1; i < 10; i++) {
                sailServerFeign.refreshCookie();
            }
        }

    }


}
