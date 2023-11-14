package com.coatardbul.stock.task;

import com.coatardbul.baseCommon.constants.AiStrategyEnum;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.JsonUtil;
import com.coatardbul.baseService.service.EsTemplateDataService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.EsTemplateConfigMapper;
import com.coatardbul.stock.model.bo.UpLimitScanStrategyBo;
import com.coatardbul.stock.model.dto.StockEmotionDayDTO;
import com.coatardbul.stock.model.dto.StockPredictDto;
import com.coatardbul.stock.service.es.EsTaskService;
import com.coatardbul.stock.service.statistic.StockCronRefreshService;
import com.coatardbul.stock.service.statistic.StockPredictService;
import com.coatardbul.stock.service.statistic.StockSpecialStrategyService;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import com.coatardbul.stock.service.statistic.minuteStatic.StockMinuteEmotinStaticService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
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
public class MinuterEmotionXxlJob {
    @Autowired
    StockMinuteEmotinStaticService stockMinuteEmotinStaticService;
    @Autowired
    StockSpecialStrategyService stockSpecialStrategyService;

    @Autowired
    StockCronRefreshService stockCronRefreshService;
    @Autowired
    EsTemplateConfigMapper esTemplateConfigMapper;
    @Autowired
    EsTemplateDataService esTemplateDataService;
    @Autowired
    RiverRemoteService riverRemoteService;
    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    StockPredictService stockPredictService;

    @XxlJob("minuterEmotionJobHandler")
    public void minuterEmotionJobHandler() throws Exception {
        String param = XxlJobHelper.getJobParam();
        log.info("分钟情绪定时任务开始,传递参数为：" + param);
        if (StringUtils.isNotBlank(param)) {
            StockEmotionDayDTO stockEmotionDayDTO = JsonUtil.readToValue(param, StockEmotionDayDTO.class);
            stockEmotionDayDTO.setDateStr(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD));
            //延后五分钟
            String minute = DateTimeUtil.getMinute(DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM), -5);
            stockEmotionDayDTO.setTimeStr(minute);
            log.info(",传递参数为：" + stockEmotionDayDTO.toString());
            stockMinuteEmotinStaticService.refreshDay(stockEmotionDayDTO);
        }
        log.info("分钟情绪定时任务结束");

    }


    /**
     * 自动刷新池,每日添加股票信息
     *
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @XxlJob("dayAddStockJobHandle")
    public void dayAddStockJobHandle() {
        String param = XxlJobHelper.getJobParam();
        log.info("涨幅大于几数据开始" + param);

        String dateStr = "";
        UpLimitScanStrategyBo dto = new UpLimitScanStrategyBo();
        if (StringUtils.isNotBlank(param)) {
            dto = JsonUtil.readToValue(param, UpLimitScanStrategyBo.class);
            dateStr = dto.getDateStr();
            if (!StringUtils.isNotBlank(dto.getTimeBeginStr())) {
                dto.setTimeBeginStr("10:40");
                dto.setTimeEndStr("14:40");
            }
            if (!StringUtils.isNotBlank(dateStr)) {
                dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            }
        }
        String timeStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM);
        if (timeStr.compareTo(dto.getTimeBeginStr()) >= 0 && timeStr.compareTo(dto.getTimeEndStr()) <= 0) {
            stockCronRefreshService.dayAddStockJob(dateStr);
        }

        log.info("涨幅大于几数据结束" + param);

    }


    /**
     * 昨日涨停，涨停
     */
    @XxlJob("aiStrategyJobHandle")
    public void aiStrategyJobHandle() {
        String param = XxlJobHelper.getJobParam();
        log.info("涨停，昨曾，埋伏开始" + param);
        String dateStr = "";
        UpLimitScanStrategyBo dto = new UpLimitScanStrategyBo();
        if (StringUtils.isNotBlank(param)) {
            dto = JsonUtil.readToValue(param, UpLimitScanStrategyBo.class);
            dateStr = dto.getDateStr();
            if (!StringUtils.isNotBlank(dto.getTimeBeginStr())) {
                dto.setTimeBeginStr("10:40");
                dto.setTimeEndStr("14:40");
            }
            if (!StringUtils.isNotBlank(dateStr)) {
                dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            }
        }
        String timeStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM);
        if (timeStr.compareTo(dto.getTimeBeginStr()) >= 0 && timeStr.compareTo(dto.getTimeEndStr()) <= 0) {
            log.info("涨停，昨曾，埋伏开始执行" + dateStr + "  " + timeStr);
            String specialDay = riverRemoteService.getSpecialDay(dateStr, -3);
            StockPredictDto sdp = new StockPredictDto();
            sdp.setBeginDate(specialDay);
            sdp.setEndDate(dateStr);
            sdp.setHoleDay(2);
            sdp.setSaleTime("11:29");
            sdp.setAiStrategySign(AiStrategyEnum.UPLIMIT_AMBUSH.getCode());
            stockPredictService.execute(sdp);


            StockPredictDto temp = new StockPredictDto();
            BeanUtils.copyProperties(sdp, temp);
            temp.setAiStrategySign(AiStrategyEnum.HAVE_UPLIMIT_AMBUSH.getCode());
            stockPredictService.execute(temp);


        }
        log.info("涨停，昨曾，埋伏结束");

    }

    /**
     * 低开下影线，低开短下长上影，其他
     *
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @XxlJob("minuterStrategyScanPlateAddJobHandle")
    public void minuterStrategyScanPlateAddJobHandle() {
        String param = XxlJobHelper.getJobParam();
        log.info("低开下影线，低开短下长上影，其他开始" + param);
        String dateStr = "";
        UpLimitScanStrategyBo dto = new UpLimitScanStrategyBo();
        if (StringUtils.isNotBlank(param)) {
            dto = JsonUtil.readToValue(param, UpLimitScanStrategyBo.class);
            dateStr = dto.getDateStr();
            if (!StringUtils.isNotBlank(dto.getTimeBeginStr())) {
                dto.setTimeBeginStr("10:40");
                dto.setTimeEndStr("14:40");
            }
            if (!StringUtils.isNotBlank(dateStr)) {
                dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
            }
        }
        String timeStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.HH_MM);
        if (timeStr.compareTo(dto.getTimeBeginStr()) >= 0 && timeStr.compareTo(dto.getTimeEndStr()) <= 0) {
            log.info("低开下影线，低开短下长上影，其他开始执行" + dateStr + "  " + timeStr);

            stockCronRefreshService.addDksyxPlateInfo(dateStr);
            stockCronRefreshService.addZcPlateInfo(dateStr);
            stockCronRefreshService.addDkdxcsyPlateInfo(dateStr);
            stockCronRefreshService.addXlPlateInfo(dateStr);
        }
        log.info("低开下影线，低开短下长上影，其他结束");
    }

    @Autowired
    EsTaskService esTaskService;

    /**
     * 涨幅同步到es上
     *
     * @throws ScriptException
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws InterruptedException
     * @throws ParseException
     */
    @XxlJob("increaseSyncEsJobHandle")
    public void increaseSyncEsJobHandle() throws ScriptException, IOException, ParseException, InterruptedException, NoSuchMethodException {
        log.info("涨幅数据同步es开始");

        esTaskService.increaseSyncEsJobHandle();

        log.info("涨幅数据同步es结束");
    }

}
