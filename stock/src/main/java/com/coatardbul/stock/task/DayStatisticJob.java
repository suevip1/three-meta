package com.coatardbul.stock.task;

import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.EsTemplateDataService;
import com.coatardbul.stock.mapper.EsTemplateConfigMapper;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.dto.StockTradeLoginDTO;
import com.coatardbul.stock.service.StockUserBaseService;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.coatardbul.stock.service.es.EsTaskService;
import com.coatardbul.stock.service.statistic.StockBaseService;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.StockSpecialStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.dayStatic.StockDayStaticService;
import com.coatardbul.stock.service.statistic.dayStatic.scatter.ScatterDayUpLimitCallAuctionService;
import com.coatardbul.stock.service.statistic.dayStatic.scatter.StockScatterService;
import com.coatardbul.stock.service.statistic.trade.StockTradeUserService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/17
 *
 * @author Su Xiaolei
 */
@Slf4j
@Component
public class DayStatisticJob {


    @Autowired
    StockStrategyService stockStrategyService;
    @Autowired
    StockScatterService stockScatterService;
    @Autowired
    StockDayStaticService stockDayStaticService;

    @Autowired
    ScatterDayUpLimitCallAuctionService stockScatterUpLimitService;

    @Autowired
    StockTradeUserService stockTradeUserService;

    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    StockUserBaseService stockUserBaseService;
    @Autowired
    StockCronRefreshService stockCronRefreshService;


    @Autowired
    StockSpecialStrategyService stockSpecialStrategyService;

    @Autowired
    EsTemplateConfigMapper esTemplateConfigMapper;
    @Autowired
    EsTemplateDataService esTemplateDataService;
    @XxlJob("dayUpDownJobHandler")
    public void dayUpDownJobHandler() throws IllegalAccessException, ParseException {
        String param = XxlJobHelper.getJobParam();
        log.info("刷新每日涨跌统计数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新每日涨跌统计数据参数" + JsonUtil.toJson(dto));
            stockDayStaticService.refreshDay(dto);
        }
        log.info("刷新每日涨跌统计数据结束");
    }

    @XxlJob("dayCallAuctionIncreaseJobHandler")
    public void dayCallAuctionIncreaseJobHandler() throws IllegalAccessException, ParseException {
        String param = XxlJobHelper.getJobParam();
        log.info("刷新每日集合竞价涨幅大于5数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新每日集合竞价涨幅大于5数据参数" + JsonUtil.toJson(dto));
            stockDayStaticService.refreshDay(dto);
        }
        log.info("刷新每日集合竞价涨幅大于5数据结束");
    }


    @XxlJob("dayTwoUpLimitJobHandler")
    public void dayTwoUpLimitJobHandler() throws IllegalAccessException, ParseException {
        String param = XxlJobHelper.getJobParam();
        log.info("刷新两板以上集合竞价数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新两板以上集合竞价数据参数" + JsonUtil.toJson(dto));
            stockScatterUpLimitService.refreshDay(dto);
        }
        log.info("刷新两板以上集合竞价数据开始");
    }





    @XxlJob("dayMarketValueUpLimitJobHandler")
    public void dayMarketValueUpLimitJobHandler() throws IllegalAccessException, ParseException {
        String param = XxlJobHelper.getJobParam();
        log.info("刷新涨停市值散点数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新涨停市值散点数据参数" + JsonUtil.toJson(dto));
            stockScatterService.refreshDay(dto);
        }
        log.info("刷新涨停市值散点数据结束");
    }


    /**
     * 自动登陆
     *
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @XxlJob("dayAutoLoginJobHandler")
    public void dayAutoLoginJobHandler() {
        int num = 10;
        while (num>0) {
            try {
                StockTradeLoginDTO dto=new StockTradeLoginDTO();
                String defaultTradeUser = stockUserBaseService.getDefaultTradeUser();
                dto.setUserId(defaultTradeUser);
                stockTradeUserService.login(dto);
                break;
            } catch (Exception e) {
                num--;
            }
        }

    }


    /**
     * 历史埋伏，历史二板以上，一次操作
     *
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @XxlJob("hisStrategyScanPlateAddJobHandle")
    public void hisStrategyScanPlateAddJobHandle() {

        String dateStr = "";
        dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        stockCronRefreshService.addMultiDayAmbushPlateInfo(dateStr);
        stockCronRefreshService.addHisTwoUpLimitAbovePlateInfo(dateStr);
        stockCronRefreshService.ambushCallauctionRob(dateStr);


    }

    @Autowired
    StockBaseService stockBaseService;


    @Autowired
    EsTaskService esTaskService;
    /**
     * 定量新增股票信息
     */
    @XxlJob("extraAllStockBaseJobHandle")
    public void extraAllStockBaseJobHandle() {
        log.info("定量新增股票信息开始" );
        stockBaseService.extraAddProcess();
        log.info("定量新增股票信息结束");

    }

    /**
     * 定量增加转债信息
     */
    @XxlJob("addConvertBondProcessJobHandle")
    public void addConvertBondProcessJobHandle() {
        log.info("定量增加转债信息开始" );
        stockBaseService.addConvertBondProcess();
        log.info("定量增加转债信息结束" );

    }

    /**
     * 竞价数据同步es
     */
    @XxlJob("auctionSyncEsJobHandle")
    public void auctionSyncEsJobHandle() throws ScriptException, IOException, NoSuchMethodException, InterruptedException, ParseException {
        log.info("竞价数据同步es开始" );
        esTaskService.auctionSyncEsJobHandle();
        log.info("竞价数据同步es结束" );
    }


    /**
     * 同花顺行业数据同步es
     */
    @XxlJob("industryDataSyncEsJobHandle")
    public void industryDataSyncEsJobHandle() throws ScriptException, IOException, NoSuchMethodException, InterruptedException, ParseException {
        log.info("同花顺行业数据同步es开始" );
        esTaskService.industryDataSyncEsJobHandle();
        log.info("同花顺行业数据同步es结束" );
    }


}
