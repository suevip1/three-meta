package com.coatardbul.stock.service.base;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseCommon.model.bo.StrategyQueryBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseCommon.util.ReflexUtil;
import com.coatardbul.baseService.entity.feign.StockTemplateQueryDTO;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.StockStrategyCommonService;
import com.coatardbul.stock.common.util.StockStaticModuleUtil;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.model.entity.AccountBase;
import com.coatardbul.stock.model.entity.StockStaticTemplate;
import com.coatardbul.stock.service.StockUserBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Note:策略处理，单一指向同花顺问句查询
 * <p>
 * Date: 2022/1/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockStrategyService  extends StockStrategyCommonService {
    @Autowired
    RiverServerFeign riverServerFeign;

    @Autowired
    AccountBaseMapper accountBaseMapper;
    @Autowired
    StockUserBaseService stockUserBaseService;
    @Autowired
    public void refreshCookie() {
        String userId = stockUserBaseService.getDefaultTradeUser();
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userId, CookieTypeEnum.TONG_HUA_SHUN.getType());
        cookieValue=accountBase.getCookie();
    }

    public void setCookieValue(String cookieValue) {
        this.cookieValue = cookieValue;
    }




    /**
     * 将请求中的dto转换成策略对象
     *
     * @param dto                  抽象请求数据
     * @param defaultStrategyQuery 策略对象
     */
    @Override
    public void setRequestInfo(StockStrategyQueryDTO dto, StrategyQueryBO defaultStrategyQuery) {
        if (dto.getPageSize() != null && dto.getPage() != null) {
            defaultStrategyQuery.setPerpage(dto.getPageSize());
            defaultStrategyQuery.setPage(dto.getPage());
        } else {
            defaultStrategyQuery.setPerpage(100);
            defaultStrategyQuery.setPage(1);
        }
        defaultStrategyQuery.setSort_key(dto.getOrderStr());
        defaultStrategyQuery.setSort_order(dto.getOrderBy());
        // 此接口可以通过调用river获取实时动态数据
        if (StringUtils.isNotBlank(dto.getQueryStr())) {
            defaultStrategyQuery.setQuestion(dto.getQueryStr());
        } else {
            //feign
            StockTemplateQueryDTO stockTemplateQueryDto = new StockTemplateQueryDTO();
            stockTemplateQueryDto.setId(dto.getRiverStockTemplateId());
            stockTemplateQueryDto.setDateStr(dto.getDateStr());
            stockTemplateQueryDto.setTimeStr(dto.getTimeStr());
            stockTemplateQueryDto.setStockCode(dto.getStockCode());
            stockTemplateQueryDto.setObjectSign(dto.getRiverStockTemplateSign());
            stockTemplateQueryDto.setThemeStr(dto.getThemeStr());
            stockTemplateQueryDto.setStockScript(dto.getStockTemplateScript());
            CommonResult<String> riverServerFeignResult = riverServerFeign.getQuery(stockTemplateQueryDto);
            if (riverServerFeignResult != null) {
                defaultStrategyQuery.setQuestion(riverServerFeignResult.getData());
            }
        }

    }

    /**
     * 获取模型对象中的模板id集合
     *
     * @param stockStaticTemplate
     * @return
     * @throws IllegalAccessException
     */
    public List<String> getTemplateIdList(StockStaticTemplate stockStaticTemplate) throws IllegalAccessException {
        List<String> result = new ArrayList<>();
        //根据标识获取对应的对象解析id数据
        Class classBySign = StockStaticModuleUtil.getClassBySign(stockStaticTemplate.getObjectSign());
        Object o = JsonUtil.readToValue(stockStaticTemplate.getObjectStr(), classBySign);
        //获取类里面的所有属性集合
        ReflexUtil.singleReadAStringAttributeList(o, result);
        return result;
    }


}
