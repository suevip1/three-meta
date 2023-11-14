package com.coatardbul.stock.task.powerJob;

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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.annotation.PowerJobHandler;
import tech.powerjob.worker.core.processor.TaskContext;

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
public class PowerJobDayStatisticJob {


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
     @PowerJobHandler(name ="dayUpDownJobHandler")
    public void dayUpDownJobHandler(TaskContext context) throws IllegalAccessException, ParseException {
        String param = context.getJobParams();
        log.info("刷新每日涨跌统计数据开始" + param);
        if (StringUtils.isNotBlank(context.getJobParams())) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新每日涨跌统计数据参数" + JsonUtil.toJson(dto));
            stockDayStaticService.refreshDay(dto);
        }
        log.info("刷新每日涨跌统计数据结束");
    }

     @PowerJobHandler(name ="dayCallAuctionIncreaseJobHandler")
    public void dayCallAuctionIncreaseJobHandler(TaskContext context) throws IllegalAccessException, ParseException {
        String param = context.getJobParams();
        log.info("刷新每日集合竞价涨幅大于5数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新每日集合竞价涨幅大于5数据参数" + JsonUtil.toJson(dto));
            stockDayStaticService.refreshDay(dto);
        }
        log.info("刷新每日集合竞价涨幅大于5数据结束");
    }


     @PowerJobHandler(name ="dayTwoUpLimitJobHandler")
    public void dayTwoUpLimitJobHandler(TaskContext context) throws IllegalAccessException, ParseException {
        String param = context.getJobParams();
        log.info("刷新两板以上集合竞价数据开始" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO dto = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            log.info("刷新两板以上集合竞价数据参数" + JsonUtil.toJson(dto));
            stockScatterUpLimitService.refreshDay(dto);
        }
        log.info("刷新两板以上集合竞价数据开始");
    }





     @PowerJobHandler(name ="dayMarketValueUpLimitJobHandler")
    public void dayMarketValueUpLimitJobHandler(TaskContext context) throws IllegalAccessException, ParseException {
        String param = context.getJobParams();
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
     @PowerJobHandler(name ="dayAutoLoginJobHandler")
    public void dayAutoLoginJobHandler(TaskContext context) {
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
     @PowerJobHandler(name ="hisStrategyScanPlateAddJobHandle")
    public void hisStrategyScanPlateAddJobHandle(TaskContext context) {

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
     @PowerJobHandler(name ="extraAllStockBaseJobHandle")
    public void extraAllStockBaseJobHandle(TaskContext context) {
        log.info("定量新增股票信息开始" );
        stockBaseService.extraAddProcess();
        log.info("定量新增股票信息结束");

    }

    /**
     * 定量增加转债信息
     */
     @PowerJobHandler(name ="addConvertBondProcessJobHandle")
    public void addConvertBondProcessJobHandle(TaskContext context) {
        log.info("定量增加转债信息开始" );
        try {
            stockBaseService.addConvertBondProcess();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("定量增加转债信息结束" );

    }

    /**
     * 竞价数据同步es
     */
     @PowerJobHandler(name ="auctionSyncEsJobHandle")
    public void auctionSyncEsJobHandle(TaskContext context)  {
        log.info("竞价数据同步es开始" );
        try {
            esTaskService.auctionSyncEsJobHandle();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("竞价数据同步es结束" );
    }


    /**
     * 同花顺行业数据同步es
     */
     @PowerJobHandler(name ="industryDataSyncEsJobHandle")
    public void industryDataSyncEsJobHandle(TaskContext context)  {
        log.info("同花顺行业数据同步es开始" );
        try {
            esTaskService.industryDataSyncEsJobHandle();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("同花顺行业数据同步es结束" );
    }


}
