package com.coatardbul.stock.service.es;

import com.coatardbul.baseCommon.api.CommonResult;
import com.coatardbul.baseCommon.constants.EsTemplateConfigEnum;
import com.coatardbul.baseCommon.model.dto.EsTemplateConfigDTO;
import com.coatardbul.baseCommon.model.entity.DictInfo;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseService.entity.bo.es.EsIndustryDataBo;
import com.coatardbul.baseService.entity.feign.StockTimeInterval;
import com.coatardbul.baseService.feign.RiverServerFeign;
import com.coatardbul.baseService.service.EsTemplateDataService;
import com.coatardbul.stock.mapper.EsTemplateConfigMapper;
import com.coatardbul.baseCommon.model.entity.EsTemplateConfig;
import com.coatardbul.stock.service.statistic.business.StockVerifyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.script.ScriptException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/11/14
 *
 * @author Su Xiaolei
 */
@Component
@Slf4j
public class EsTaskService {

    @Autowired
    StockVerifyService stockVerifyService;
    @Autowired
    EsTemplateConfigMapper esTemplateConfigMapper;

    @Autowired
    EsTemplateDataService esTemplateDataService;


    @Autowired
    RiverServerFeign riverServerFeign;
    @Autowired
    EsIndustryDataService esIndustryDataService;

    public void auctionSyncEsJobHandle() throws ScriptException, IOException, NoSuchMethodException, InterruptedException, ParseException {
        String dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        if (stockVerifyService.isIllegalDate(dateStr)) {
            return;
        }
        List<EsTemplateConfig> esTemplateConfigs = esTemplateConfigMapper.selectAllByEsDataType(EsTemplateConfigEnum.TYPE_AUCTION.getSign());
        for (int i = 0; i < esTemplateConfigs.size(); i++) {
            auctionSync(dateStr, esTemplateConfigs.get(i));
            Thread.sleep(EsTemplateConfigEnum.getTimeInterval(esTemplateConfigs.get(i).getEsDataLevel()));
        }
    }

    private void auctionSync(String dateStr, EsTemplateConfig esTemplateConfig) throws ScriptException, IOException, NoSuchMethodException {
        EsTemplateConfigDTO s1 = new EsTemplateConfigDTO();
        BeanUtils.copyProperties(esTemplateConfig, s1);
        s1.setDateStr(dateStr);
        s1.setRiverStockTemplateId(esTemplateConfig.getTemplateId());
        esTemplateDataService.syncData(s1);
    }

    public void industryDataSyncEsJobHandle() throws ScriptException, IOException, NoSuchMethodException, InterruptedException, ParseException {
        String dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        if (stockVerifyService.isIllegalDate(dateStr)) {
            return;
        }
        String yearStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY);

        Map<String, String> map = new HashMap<String, String>();
        map.put("busiType", "tongHuaShun_industry_code");
        CommonResult<List<DictInfo>> infoByType = riverServerFeign.getInfoByType(map);
        for (DictInfo dictInfo : infoByType.getData()) {
            EsIndustryDataBo esIndustryDataBo = new EsIndustryDataBo();
            esIndustryDataBo.setYearStr(yearStr);
            esIndustryDataBo.setBkCode(dictInfo.getSignKey());
            esIndustryDataBo.setBkName(dictInfo.getSignValue());
            esIndustryDataService.syncData(esIndustryDataBo);
            Thread.sleep(5 * 1000);
        }
    }

    public void increaseSyncEsJobHandle() throws ScriptException, IOException, NoSuchMethodException, InterruptedException, ParseException {
        String dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        if (stockVerifyService.isIllegalDate(dateStr)) {
            return;
        }
        List<EsTemplateConfig> esTemplateConfigs = esTemplateConfigMapper.selectAllByEsDataType(EsTemplateConfigEnum.TYPE_DAY.getSign());
        for (int i = 0; i < esTemplateConfigs.size(); i++) {
            auctionSync(dateStr, esTemplateConfigs.get(i));
            Thread.sleep(EsTemplateConfigEnum.getTimeInterval(esTemplateConfigs.get(i).getEsDataLevel()));
        }
    }

    public void dayCountSyncEsJobHandle() throws ParseException, ScriptException, IOException, NoSuchMethodException, InterruptedException {
        String dateStr = DateTimeUtil.getDateFormat(new Date(), DateTimeUtil.YYYY_MM_DD);
        if (stockVerifyService.isIllegalDate(dateStr)) {
            return;
        }
        List<EsTemplateConfig> esTemplateConfigs = esTemplateConfigMapper.selectAllByEsDataType(EsTemplateConfigEnum.TYPE_DAY_COUNT.getSign());
        for (int i = 0; i < esTemplateConfigs.size(); i++) {
            auctionSync(dateStr, esTemplateConfigs.get(i));
            Thread.sleep(EsTemplateConfigEnum.getTimeInterval(esTemplateConfigs.get(i).getEsDataLevel()));
        }
    }

    public void minuterIncreaseSyncEsJobHandle(String dateStr, String timeStr, Integer interval) throws ParseException, ScriptException, IOException, NoSuchMethodException {

        if (timeStr.compareTo("09:30") < 0) {
            return;
        }
        if (stockVerifyService.isIllegalDate(dateStr)) {
            return;
        }
        StockTimeInterval stockTimeInterval = new StockTimeInterval();
        stockTimeInterval.setIntervalType(interval);
        CommonResult<List<String>> timeIntervalList = riverServerFeign.getTimeIntervalList(stockTimeInterval);
        if (timeIntervalList != null && timeIntervalList.getData() != null) {
            List<EsTemplateConfig> esTemplateConfigs = esTemplateConfigMapper.selectAllByEsDataType(EsTemplateConfigEnum.TYPE_MINUTER.getSign());
            for (int i = 0; i < esTemplateConfigs.size(); i++) {
                List<String> data = timeIntervalList.getData();
                int finalIndex = data.size() - 1;
                for (int j = 1; j < data.size(); j++) {
                    if (timeStr.compareTo(data.get(j)) < 0) {
                        finalIndex = j - 1;
                        break;
                    }
                }
                for (int j = 0; j <= finalIndex; j++) {
                    //查询是否有值，没有同步数据
                    EsTemplateConfigDTO s1 = new EsTemplateConfigDTO();
                    BeanUtils.copyProperties(esTemplateConfigs.get(i), s1);
                    s1.setDateStr(dateStr);
                    s1.setRiverStockTemplateId(esTemplateConfigs.get(i).getTemplateId());
                    s1.setTimeStr(data.get(j));
                    Long count = esTemplateDataService.getCount(s1);
                    if(count==null ||count==0){
                        esTemplateDataService.syncData(s1);
                    }
                }

            }


        }
    }
}
