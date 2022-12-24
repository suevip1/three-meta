package com.coatardbul.stock.service.statistic.business;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.exception.BusinessException;
import com.coatardbul.baseCommon.model.bo.CronRefreshConfigBo;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.entity.feign.StockTimeInterval;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.CronRefreshService;
import com.coatardbul.baseService.service.romote.RiverRemoteService;
import com.coatardbul.stock.mapper.StockStaticTemplateMapper;
import com.coatardbul.stock.model.entity.StockStaticTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/2/8
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockVerifyService {

    @Autowired
    RiverRemoteService riverRemoteService;
    @Resource
    public CronRefreshService cronRefreshService;
    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    StockStaticTemplateMapper stockStaticTemplateMapper;




    /**
     * 验证日期
     * 不能超过当前日期
     *
     * @param dateStr YYYY-MM-DD
     * @throws ParseException
     */
    public void verifyDateStr(String  dateStr) throws ParseException {
        Date date = DateTimeUtil.parseDateStr(dateStr, DateTimeUtil.YYYY_MM_DD);
        if (new Date().compareTo(date) < 0) {
            throw new BusinessException("当前日期"+dateStr+"不合法，不能超过当前时间");
        }
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dateStr,dateStr);
        if (dateIntervalList == null || dateIntervalList.size() == 0) {
            throw new BusinessException("非交易日");
        }
    }

    /**
     * 验证非法日期
     * @param dateStr YYYY-MM-DD
     * @return true 非法日期
     * @throws ParseException
     */
    public Boolean isIllegalDate(String  dateStr) throws ParseException {
        Date date = DateTimeUtil.parseDateStr(dateStr, DateTimeUtil.YYYY_MM_DD);
        if (new Date().compareTo(date) < 0) {
            return true;
        }
        List<String> dateIntervalList = riverRemoteService.getDateIntervalList(dateStr,dateStr);
        if (dateIntervalList == null || dateIntervalList.size() == 0) {
            return true;
        }
        return false;
    }


    /**
     * 验证日期
     * 不能超过当前日期
     *
     * @param dateStr YYYY-MM-DD
     * @throws ParseException
     */
    public void verifyDateTimeStr(String  dateStr,String timeStr) throws ParseException {
        Date date=DateTimeUtil.parseDateStr(dateStr+timeStr,DateTimeUtil.YYYY_MM_DD+DateTimeUtil.HH_MM);
        if (new Date().compareTo(date) < 0) {
            throw new BusinessException("当前"+dateStr+timeStr+"不合法，不能超过当前时间");
        }
    }

    /**
     * 是否非法日期，
     * @param dateStr
     * @param timeStr
     * @return
     * @throws ParseException
     */
    public Boolean isIllegalDateTimeStr(String  dateStr,String timeStr) throws ParseException {
        CronRefreshConfigBo cronRefreshConfigBo = cronRefreshService.getCronRefreshConfigBo();

//        Date date=DateTimeUtil.parseDateStr(dateStr+timeStr,DateTimeUtil.YYYY_MM_DD+DateTimeUtil.HH_MM);
//        if (new Date().compareTo(date) < 0) {
//            return true;
//        }
        if(timeStr.compareTo(cronRefreshConfigBo.getCronAmBeginTime())<0){
            return true;
        }
        if(timeStr.compareTo(cronRefreshConfigBo.getCronAmEndTime())>0&&timeStr.compareTo(cronRefreshConfigBo.getCronPmBeginTime())<0){
            return true;
        }
        if(timeStr.compareTo(cronRefreshConfigBo.getCronPmEndTime())>0){
            return true;
        }
        return false;
    }


    public StockStaticTemplate verifyObjectSign(String objectSign) {
        List<StockStaticTemplate> stockStaticTemplates = stockStaticTemplateMapper.selectAllByObjectSign(objectSign);
        if (stockStaticTemplates == null || stockStaticTemplates.size() == 0) {
            throw new BusinessException("对象标识异常");
        }
        //模型策略数据
        return stockStaticTemplates.get(0);
    }

    /**
     * 获取远程间隔数据
     *
     * @param timeInterval
     * @return
     */
    public List<String> getRemoteTimeInterval(Integer timeInterval) {
        StockTimeInterval stockTimeInterval = new StockTimeInterval();
        stockTimeInterval.setIntervalType(timeInterval);
        CommonResult<List<String>> timeIntervalList = riverServerFeign.getTimeIntervalList(stockTimeInterval);
        List<String> timeIntervalListData = timeIntervalList.getData();
        if (timeIntervalListData == null || timeIntervalListData.size() == 0) {
            throw new BusinessException("不支持的时间间隔，请更新时间间隔数据");
        }
        return timeIntervalListData;
    }

}
