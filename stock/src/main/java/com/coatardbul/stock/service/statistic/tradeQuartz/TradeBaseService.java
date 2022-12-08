package com.coatardbul.stock.service.statistic.tradeQuartz;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.SimulateTypeEnum;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.service.DataServiceBridge;
import com.coatardbul.baseService.service.StockParseAndConvertService;
import com.coatardbul.stock.feign.BaseServerFeign;
import com.coatardbul.stock.mapper.StockStrategyWatchMapper;
import com.coatardbul.stock.mapper.StockTradeBuyConfigMapper;
import com.coatardbul.stock.mapper.StockTradeDetailMapper;
import com.coatardbul.stock.mapper.StockTradeSellJobMapper;
import com.coatardbul.stock.mapper.StockTradeStrategyMapper;
import com.coatardbul.stock.mapper.StockTradeUrlMapper;
import com.coatardbul.baseService.entity.bo.PreTradeDetail;
import com.coatardbul.stock.model.bo.trade.StockBaseDetail;
import com.coatardbul.stock.model.bo.trade.TradeAllConfigDetail;
import com.coatardbul.stock.model.entity.StockTradeConfig;
import com.coatardbul.stock.model.entity.StockTradeDateSwitch;
import com.coatardbul.stock.model.entity.StockTradeDetail;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.romote.RiverRemoteService;
import com.coatardbul.stock.service.statistic.DataFactory;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.trade.StockTradeAssetPositionService;
import com.coatardbul.stock.service.statistic.trade.StockTradeBaseService;
import com.coatardbul.stock.service.statistic.trade.StockTradeConfigService;
import com.coatardbul.stock.service.statistic.trade.StockTradeDateSwitchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/8/17
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class TradeBaseService {
    @Autowired
    StockTradeUrlMapper stockTradeUrlMapper;
    @Autowired
    StockTradeBaseService stockTradeBaseService;
    @Autowired
    BaseServerFeign baseServerFeign;
    @Autowired
    StockTradeSellJobMapper stockTradeSellJobMapper;
    @Autowired
    StockStrategyWatchMapper stockStrategyWatchMapper;
    @Autowired
    StockTradeConfigService stockTradeConfigService;
    @Autowired
    StockTradeBuyConfigMapper stockTradeBuyConfigMapper;
    @Autowired
    RiverRemoteService riverRemoteService;

    @Autowired
    StockVerifyService stockVerifyService;

    @Autowired
    StockTradeDateSwitchService stockTradeDateSwitchService;

    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    StockParseAndConvertService stockParseAndConvertService;

    @Autowired
    StockTradeDetailMapper stockTradeDetailMapper;
    @Autowired
    StockTradeAssetPositionService stockTradeAssetPositionService;
    @Autowired
    StockTradeStrategyMapper stockTradeStrategyMapper;

    @Autowired
    DataFactory dataFactory;

//    public Boolean tradeProcess(BigDecimal userMoney, BigDecimal buyNum, String code, String name) {
//        Boolean flag = false;
//
//        TradeAllConfigDetail commonTradeInfo = null;
//        try {
//            commonTradeInfo = getCommonTradeInfo(code);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            return flag;
//        }
//        //预交易
//        PreTradeDetail preTradeDetail = computedTradeInfo(userMoney, buyNum, commonTradeInfo);
//        //买入卖出分类计算
//
//        // 处理中
//
//
//        //后缀
//        suffixHandle(commonTradeInfo,preTradeDetail);
//        return flag;
//    }


    public PreTradeDetail computedTradeInfo(BigDecimal userMoney, BigDecimal buyNum, TradeAllConfigDetail commonTradeInfo) {
        PreTradeDetail result = new PreTradeDetail();
        BeanUtils.copyProperties(commonTradeInfo, result);
        BeanUtils.copyProperties(commonTradeInfo.getStockBaseDetail(), result);
        result.setUserMoney(userMoney);
        return result;
    }

    /**
     * 后缀处理
     */
    public   void suffixHandle(TradeAllConfigDetail commonTradeInfo , PreTradeDetail preTradeDetail, Boolean flag){

        if(flag){
            // todo 添加仓位  注意买卖
//        stockTradeAssetPositionService.addAssetInfo(preTradeDetail.getBuyNum(), upLimitPrice.getUpLimitPrice(), code, name, date);


            saveStockTradeDetail(commonTradeInfo,preTradeDetail);
        }

    }


    private void saveStockTradeDetail( TradeAllConfigDetail commonTradeInfo ,  PreTradeDetail preTradeDetail ){
        // 记录详情
        StockTradeDetail stockTradeDetail = new StockTradeDetail();
        stockTradeDetail.setId(baseServerFeign.getSnowflakeId());
        stockTradeDetail.setCode(preTradeDetail.getCode());
        stockTradeDetail.setName(preTradeDetail.getName());
        stockTradeDetail.setTradeType(preTradeDetail.getTradeType());
        stockTradeDetail.setSimulateType(commonTradeInfo.getDefaultStockTradeConfig().getSimulateType());
        stockTradeDetail.setTradeDate(commonTradeInfo.getDate());
        stockTradeDetail.setTradeTime(commonTradeInfo.getTime());
        stockTradeDetail.setDelegateNum(preTradeDetail.getTradeNum().toString());
        stockTradeDetail.setTradeNum(preTradeDetail.getTradeNum().toString());
        stockTradeDetail.setDelegatePrice(preTradeDetail.getPrice().toString());

        //todo 根据策略查询
//        stockTradeDetail.setTradePrice();
//        stockTradeDetail.setTradeAllAmount();
//        stockTradeDetail.setMsg();
        stockTradeDetailMapper.insert(stockTradeDetail);
    }


    public TradeAllConfigDetail getCommonTradeInfo(String code) {
        TradeAllConfigDetail result = new TradeAllConfigDetail();


        // 获取当前的账号
        StockTradeConfig defaultStockTradeConfig = stockTradeConfigService.getDefaultStockTradeConfig();

        if (defaultStockTradeConfig == null) {
            throw new IllegalArgumentException("upLimitPrice.getUpLimitPrice() 当前账户信息不能为空");
        }
        String date = "";

        String time = "";

        //模拟
        if (SimulateTypeEnum.SIMULATE.getSign().equals(defaultStockTradeConfig.getSimulateType())) {
            //获取日期
            StockTradeDateSwitch dateSwitchInfo = stockTradeDateSwitchService.getDateSwitchInfo();
            //日期
            date = dateSwitchInfo.getDate();
            time = dateSwitchInfo.getTime();
        }
        //真实交易
        if (SimulateTypeEnum.REAL.getSign().equals(defaultStockTradeConfig.getSimulateType())) {
            Date currenDate = new Date();
            date = DateTimeUtil.getDateFormat(currenDate, DateTimeUtil.YYYY_MM_DD);
            time = DateTimeUtil.getDateFormat(currenDate, DateTimeUtil.HH_MM);
        }

        //涨跌停价
        StockBaseDetail upLimitPrice = getImmediateStockBaseInfo(code, date);
        if (upLimitPrice.getUpLimitPrice() == null) {
            throw new IllegalArgumentException("upLimitPrice.getUpLimitPrice()涨停价不能为空");
        }

        result.setDefaultStockTradeConfig(defaultStockTradeConfig);
        result.setStockBaseDetail(upLimitPrice);
        result.setDate(date);
        result.setTime(time);

        return result;
    }


    /**
     * 通过tick数据获取
     * @param code
     * @param date
     * @return
     */
    public StockBaseDetail getImmediateStockBaseInfo(String code, String date) {
        StockBaseDetail result = new StockBaseDetail();
        result.setCode(code);
        DataServiceBridge build = dataFactory.build();
        String response = build.getStockInfo(code);
        Map map = new HashMap();
        build.rebuildStockDetailMap(response,map);
        if (map.size()==0) {
            return result;
        }
//        result.setName(map.get("name").toString());
        result.setLastClosePrice(new BigDecimal(map.get("lastClosePrice").toString()));
        result.setCurrPrice(new BigDecimal(map.get("newPrice").toString()));
        result.setMaxPrice(new BigDecimal(map.get("maxPrice").toString()));
        result.setMinPrice(new BigDecimal(map.get("minPrice").toString()));
        rebuild(result);
        return result;
    }



    /**
     * 获取股票基本详情
     * @param code
     * @param date
     * @return
     */
    public StockBaseDetail getStockBaseInfo(String code, String date) {
        StockBaseDetail result = new StockBaseDetail();
        result.setCode(code);
        //金额判断,股票详情
        StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
        dto.setRiverStockTemplateSign(StockTemplateEnum.STOCK_DETAIL.getSign());
        dto.setDateStr(date);
        dto.setStockCode(code);
        StrategyBO strategy = null;
        try {
            strategy = stockStrategyService.strategy(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        if (strategy == null || strategy.getTotalNum() == 0) {
            return result;
        }
        JSONObject jsonObject = strategy.getData().getJSONObject(0);
        //当日
        String dateFormat = date.replace("-", "");
        for (String key : jsonObject.keySet()) {
            if (key.contains("股票简称") ) {
                result.setName(jsonObject.getString(key));
            }
            if (key.contains("收盘价") && !key.contains(dateFormat)) {
                BigDecimal closePrice = stockParseAndConvertService.convert(jsonObject.get(key));
                result.setLastClosePrice(closePrice);
            }
            if (key.contains("收盘价") && key.contains(dateFormat)) {
                BigDecimal currPrice = stockParseAndConvertService.convert(jsonObject.get(key));
                result.setCurrPrice(currPrice);
            }
            if (key.contains("最高价") && key.contains(dateFormat)) {
                BigDecimal maxPrice = stockParseAndConvertService.convert(jsonObject.get(key));
                result.setMaxPrice(maxPrice);
            }
            if (key.contains("最低价") && key.contains(dateFormat)) {
                BigDecimal minPrice = stockParseAndConvertService.convert(jsonObject.get(key));
                result.setMinPrice(minPrice);
            }
        }
        rebuild(result);
        return result;
    }

    /**
     * 补充涨幅
     *
     * @param result
     */
    private void rebuild(StockBaseDetail result) {
        if (result.getLastClosePrice() != null) {
            //涨停价
            result.setUpLimitPrice(stockParseAndConvertService.getUpLimit(result.getLastClosePrice()));
            //跌停价
            result.setDownLimitPrice(stockParseAndConvertService.getDownLimit(result.getLastClosePrice()));
            //最高价涨幅
            BigDecimal maxPriceSubtract = result.getMaxPrice().subtract(result.getLastClosePrice());
            result.setMaxUpRate(maxPriceSubtract.divide(result.getLastClosePrice(), 2, BigDecimal.ROUND_HALF_UP));
            //最低价涨幅
            BigDecimal minPriceSubtract = result.getMinPrice().subtract(result.getLastClosePrice());
            result.setMinUpRate(minPriceSubtract.divide(result.getLastClosePrice(), 2, BigDecimal.ROUND_HALF_UP));
            //目前涨幅
            BigDecimal currPriceSubtract = result.getCurrPrice().subtract(result.getLastClosePrice());
            result.setCurrUpRate(currPriceSubtract.divide(result.getLastClosePrice(), 2, BigDecimal.ROUND_HALF_UP));
        }
    }

}
