package com.coatardbul.stock.service.statistic.trade;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.CookieTypeEnum;
import com.coatardbul.baseCommon.constants.StockWatchTypeEnum;
import com.coatardbul.baseCommon.constants.TradeSignEnum;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.trade.StockBaseDetail;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.entity.bo.StockTradeBuyTask;
import com.coatardbul.baseService.service.SnowFlakeService;
import com.coatardbul.baseService.service.StockParseAndConvertService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.AccountBaseMapper;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.mapper.StockStrategyWatchMapper;
import com.coatardbul.stock.mapper.StockTradeBuyConfigMapper;
import com.coatardbul.stock.mapper.StockTradeSellJobMapper;
import com.coatardbul.stock.mapper.StockTradeStrategyMapper;
import com.coatardbul.stock.mapper.StockTradeUrlMapper;
import com.coatardbul.stock.model.bo.QuartzBean;
import com.coatardbul.stock.model.bo.trade.StockTradeBO;
import com.coatardbul.stock.model.entity.AccountBase;
import com.coatardbul.stock.model.entity.StockBase;
import com.coatardbul.stock.model.entity.StockStrategyWatch;
import com.coatardbul.stock.model.entity.StockTradeBuyConfig;
import com.coatardbul.stock.model.entity.StockTradeSellJob;
import com.coatardbul.stock.model.entity.StockTradeSellTask;
import com.coatardbul.stock.model.entity.StockTradeStrategy;
import com.coatardbul.stock.model.entity.StockTradeUrl;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TimeBuyTradeService;
import com.coatardbul.stock.service.statistic.tradeQuartz.TradeBaseService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/6/5
 *
 * @author Su Xiaolei
 */
@Slf4j
@Service
public class StockTradeService {


    @Autowired
    StockTradeUrlMapper stockTradeUrlMapper;

    @Autowired
    TradeBaseService tradeBaseService;
    @Autowired
    StockTradeBaseService stockTradeBaseService;
    @Autowired
    SnowFlakeService snowFlakeService;
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
    StockBaseMapper stockBaseMapper;
    @Autowired
    StockTradeAssetPositionService stockTradeAssetPositionService;
    @Autowired
    StockTradeStrategyMapper stockTradeStrategyMapper;


    @Autowired
    TimeBuyTradeService timeBuyTradeService;
    @Autowired
    StockUserBaseService stockUserBaseService;
    @Autowired
    AccountBaseMapper accountBaseMapper;

    /**
     * 查询持仓
     *
     * @return
     */
    public String queryAssetAndPosition() {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        //账号信息
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_TRADE.getType());
        //路径信息
        List<StockTradeUrl> stockTradeUrls = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.ASSET_POSITION.getSign());
        if (stockTradeUrls == null || stockTradeUrls.size() == 0) {
            return null;
        }
        StockTradeUrl stockTradeUrl = stockTradeUrls.get(0);

        String url = stockTradeUrl.getUrl().replace("${validatekey}", accountBase.getParam1());
        String param = "moneyType=RMB";
        try {
            String result = stockTradeBaseService.tradeByString(url, param, userName);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String status = jsonObject.getString("Status");
            if ("0".equals(status)) {
                return jsonObject.getString("Data");
            } else {
                throw new BusinessException("登陆异常");
            }
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("登陆异常");
        }
    }

    public JSONArray getHisDealData(String beginDate, String endDate, int pageSize) {
        JSONArray hisDealData = getHisDealData(beginDate, endDate, pageSize, "");
        if (hisDealData.size() == pageSize) {
            JSONArray dwc = getHisDealData(beginDate, endDate, pageSize, hisDealData.getJSONObject(hisDealData.size() - 1).getString("Dwc"));
            hisDealData.addAll(dwc);
            while (dwc.size() == pageSize) {
                dwc = getHisDealData(beginDate, endDate, pageSize, hisDealData.getJSONObject(hisDealData.size() - 1).getString("Dwc"));
                hisDealData.addAll(dwc);
            }
        }
        return hisDealData;
    }

    public JSONArray getHisDealData(String beginDate, String endDate, int pageSize, String dwc) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        //账号信息
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_TRADE.getType());
        //路径信息
        List<StockTradeUrl> stockTradeUrls = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.HIS_DEAL_DATA.getSign());
        if (stockTradeUrls == null || stockTradeUrls.size() == 0) {
            return new JSONArray();
        }
        StockTradeUrl stockTradeUrl = stockTradeUrls.get(0);

        String url = stockTradeUrl.getUrl().replace("${validatekey}", accountBase.getParam1());
        String param = "st=" + beginDate + "&et=" + endDate + "&qqhs=" + pageSize + "&dwc=" + dwc;
        try {
            String result = stockTradeBaseService.tradeByString(url, param, userName);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String status = jsonObject.getString("Status");
            if ("0".equals(status)) {
                return renderData(jsonObject);
            } else {
                throw new BusinessException("登陆异常");
            }
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("登陆异常");
        }
    }

    private JSONArray renderData(JSONObject jsonObject) {
        JSONArray data = jsonObject.getJSONArray("Data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String zqdm = jsonObject1.getString("Zqdm");
            StockBase stockBase = stockBaseMapper.selectByPrimaryKey(zqdm);
            if(stockBase!=null){
                jsonObject1.put("theme", stockBase.getTheme());
                jsonObject1.put("industry", stockBase.getIndustry());
            }
        }
        return data;
    }

    public JSONArray getDealData(int pageSize) {
        JSONArray hisDealData = getDealData(pageSize, "");
        if (hisDealData.size() == pageSize) {
            JSONArray dwc = getDealData(pageSize, hisDealData.getJSONObject(hisDealData.size() - 1).getString("Dwc"));
            hisDealData.addAll(dwc);
            while (dwc.size() == pageSize) {
                dwc = getDealData(pageSize, hisDealData.getJSONObject(hisDealData.size() - 1).getString("Dwc"));
                hisDealData.addAll(dwc);
            }
        }
        return hisDealData;
    }

    public JSONArray getDealData(int pageSize, String dwc) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        String userName = stockUserBaseService.getCurrUserName(request);
        //账号信息
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_TRADE.getType());
        //路径信息
        List<StockTradeUrl> stockTradeUrls = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.DEAL_DATA.getSign());
        if (stockTradeUrls == null || stockTradeUrls.size() == 0) {
            return new JSONArray();
        }
        StockTradeUrl stockTradeUrl = stockTradeUrls.get(0);
        String url = stockTradeUrl.getUrl().replace("${validatekey}", accountBase.getParam1());
        String param = "qqhs=" + pageSize + "&dwc=" + dwc;
        try {
            String result = stockTradeBaseService.tradeByString(url, param, userName);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String status = jsonObject.getString("Status");
            if ("0".equals(status)) {
                return renderData(jsonObject);
            } else {
                throw new BusinessException("登陆异常");
            }
        } catch (ConnectTimeoutException e) {
            throw new BusinessException("登陆异常");
        }
    }


    private String bugSellCommon(StockTradeBO dto, String userName) {

        //账号信息
        AccountBase accountBase = accountBaseMapper.selectByUserIdAndTradeType(userName, CookieTypeEnum.DONG_FANG_CAI_FU_TRADE.getType());

        List<StockTradeUrl> stockTradeUrls = stockTradeUrlMapper.selectAllBySign(TradeSignEnum.BUY_SELL.getSign());
        if (stockTradeUrls == null || stockTradeUrls.size() == 0) {
            return null;
        }
        StockTradeUrl stockTradeUrl = stockTradeUrls.get(0);

        String url = stockTradeUrl.getUrl().replace("${validatekey}", accountBase.getParam1());

        try {
            String result = stockTradeBaseService.trade(url, dto, userName);
            log.info("交易对象" + JsonUtil.toJson(dto) + "交易返回信息" + result);
            JSONObject jsonObject = JSONObject.parseObject(result);
            String status = jsonObject.getString("Status");
            if ("0".equals(status)) {
                return jsonObject.getString("Data");
            } else {
                throw new BusinessException(jsonObject.getString("Message"));
            }
        } catch (ConnectTimeoutException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public String sell(StockTradeBO dto, String userName) {
        dto.setTradeType("S");
        return bugSellCommon(dto, userName);
    }

    public String buy(StockTradeBO dto) {
        return buy(dto, null);
    }

    public String buy(StockTradeBO dto, String userName) {
        dto.setTradeType("B");
        return bugSellCommon(dto, userName);
    }

    public void addSellInfo(StockTradeSellJob dto) {
        dto.setId(snowFlakeService.getSnowId());
        dto.setStatus(1);
        stockTradeSellJobMapper.insert(dto);
    }

    public void modifySellInfo(StockTradeSellJob dto) {
        stockTradeSellJobMapper.updateByPrimaryKeySelective(dto);
    }

    public List<StockTradeSellJob> querySellInfo(StockTradeSellJob dto) {
        List<StockTradeSellJob> stockTradeSellJobs = stockTradeSellJobMapper.selectByAll(dto);
        return stockTradeSellJobs;
    }

    public void deleteSellInfo(StockTradeSellJob dto) {
        stockTradeSellJobMapper.deleteByPrimaryKey(dto.getId());
    }

    public void syncBuyInfo() {
        List<StockStrategyWatch> stockStrategyWatches = stockStrategyWatchMapper.selectAllByType(StockWatchTypeEnum.EMAIL.getType());
        if (stockStrategyWatches.size() > 0) {
            for (StockStrategyWatch ssw : stockStrategyWatches) {
                StockTradeBuyConfig stbc = stockTradeBuyConfigMapper.selectAllByTemplateId(ssw.getTemplatedId());
                if (stbc == null) {
                    StockTradeBuyConfig stockTradeBuyConfig = new StockTradeBuyConfig();
                    stockTradeBuyConfig.setId(snowFlakeService.getSnowId());
                    stockTradeBuyConfig.setTemplateId(ssw.getTemplatedId());
                    stockTradeBuyConfig.setTemplateName(riverRemoteService.getTemplateNameById(ssw.getTemplatedId()));
                    stockTradeBuyConfigMapper.insertSelective(stockTradeBuyConfig);
                }
            }
        }
    }


    public void initBuyInfo() {
        List<StockTradeBuyConfig> stockTradeBuyConfigs = stockTradeBuyConfigMapper.selectByAll(null);
        if (stockTradeBuyConfigs != null && stockTradeBuyConfigs.size() > 0) {
            //查询持仓可用金额
            String result = queryAssetAndPosition();
            JSONArray jsonArray = JSONArray.parseArray(result);
            String kyzj = jsonArray.getJSONObject(0).getString("Kyzj");
            for (StockTradeBuyConfig stbc : stockTradeBuyConfigs) {
                if (stbc.getProportion() != null) {
                    BigDecimal multiply = new BigDecimal(kyzj).multiply(stbc.getProportion());
                    stbc.setAllMoney(multiply);
                    stbc.setSubMoney(multiply);
                    stbc.setSubNum(stbc.getAllNum());
                    stockTradeBuyConfigMapper.updateByPrimaryKeySelective(stbc);
                }
            }

        }


    }


    /**
     * 直接买入
     */
    public Boolean directBuy(BigDecimal userMoney, BigDecimal buyNum, String code, String name, String userName) {

        return timeBuyTradeService.tradeProcess(userMoney, buyNum, code, userName);
    }


    public String[] splitJobName(String jobName) {
        return jobName.split("_");
    }

    public String getJobName(StockTradeBuyTask dto) {
        return dto.getId() + "_" + dto.getStrategySign() + "_" + dto.getStockCode() + "_" + dto.getStockName();
    }

    public String getJobName(StockTradeSellTask dto) {
        return dto.getId() + "_" + dto.getStrategySign() + "_" + dto.getStockCode() + "_" + dto.getStockName();
    }

    public QuartzBean getQuartzBean(String strategySign, String jobName, String cron) {
        QuartzBean quartzBean = new QuartzBean();
        StockTradeStrategy stockTradeStrategy = stockTradeStrategyMapper.selectAllBySign(strategySign);
        quartzBean.setJobClass(stockTradeStrategy.getJobClass());
        quartzBean.setJobName(jobName);
        Date now = new Date();
        quartzBean.setStartTime(DateUtils.addSeconds(now, 10));
        quartzBean.setCronExpression(cron);
        JobDataMap map = new JobDataMap();
        quartzBean.setJobDataMap(map);
        return quartzBean;
    }


    public QuartzBean getQuartzBean(StockTradeStrategy stockTradeStrategy, String jobName, String cron) {
        QuartzBean quartzBean = new QuartzBean();
        quartzBean.setJobClass(stockTradeStrategy.getJobClass());
        quartzBean.setJobName(jobName);
        Date now = new Date();
        quartzBean.setStartTime(DateUtils.addSeconds(now, 0));
        quartzBean.setCronExpression(cron);
        JobDataMap map = new JobDataMap();
        quartzBean.setJobDataMap(map);
        return quartzBean;
    }


    public void directSell(BigDecimal sellPrice, BigDecimal sellNum, String code, String name, HttpServletRequest request) {
        String userName = stockUserBaseService.getCurrUserName(request);
        StockTradeBO stockTradeBO = new StockTradeBO();
        stockTradeBO.setStockCode(code);

        Date currenDate = new Date();
        String date = DateTimeUtil.getDateFormat(currenDate, DateTimeUtil.YYYY_MM_DD);
        //涨跌停价
        StockBaseDetail upLimitPrice = tradeBaseService.getImmediateStockBaseInfoNoProxy(code, date);
        stockTradeBO.setPrice(upLimitPrice.getSugSellPrice().toString());


        stockTradeBO.setAmount(sellNum.toString());
        stockTradeBO.setZqmc(name);
        sell(stockTradeBO, userName);
    }


}
