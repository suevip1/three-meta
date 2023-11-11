package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseCommon.constants.StockTemplateEnum;
import com.coatardbul.baseCommon.model.bo.StrategyBO;
import com.coatardbul.baseCommon.model.bo.trade.StockBaseDetail;
import com.coatardbul.baseCommon.model.dto.StockStrategyQueryDTO;
import com.coatardbul.baseCommon.util.DateTimeUtil;
import com.coatardbul.baseCommon.util.TongHuaShunUtil;
import com.coatardbul.stock.mapper.StockBaseMapper;
import com.coatardbul.stock.model.dto.StockBaseDTO;
import com.coatardbul.stock.model.entity.StockBase;
import com.coatardbul.stock.service.base.StockStrategyService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/9/8
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class StockBaseService {

    private static final String EXTRA="extra";
    private static final String ALL="all";

    @Autowired
    private StockBaseMapper stockBaseMapper;
    @Resource
    DongFangSortService dongFangSortService;
    @Autowired
    private StockStrategyService stockStrategyService;



    public void extraAddProcess() {
        addProcessCommon(EXTRA);
    }
    public void allAddProcess(){
        addProcessCommon(ALL);
    }
    public void addProcessCommon(String sign) {
            StockStrategyQueryDTO dto = new StockStrategyQueryDTO();
            dto.setRiverStockTemplateSign(StockTemplateEnum.FOR_EACH_STOCK.getSign());
            dto.setPageSize(100);
            dto.setPage(1);
            dto.setDateStr(DateTimeUtil.getDateFormat(new Date(),DateTimeUtil.YYYY_MM_DD));
            StrategyBO strategy = null;
            try {
                if(ALL.equals(sign)){
                    strategy = stockStrategyService.comprehensiveStrategy(dto);
                }
                if(EXTRA.equals(sign)){
                    strategy = stockStrategyService.strategyFirstProcess(dto);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            if (strategy != null && strategy.getTotalNum() > 0) {
                JSONArray data = strategy.getData();
                for (int j = 0; j < data.size(); j++) {
                    JSONObject jsonObject = data.getJSONObject(j);
                    String code = jsonObject.getString("code");
                    String name = jsonObject.getString("股票简称");
                    String theme = jsonObject.getString("所属概念");
                    String industry = jsonObject.getString("所属同花顺行业");
                    StockBase stockBase = new StockBase();
                    stockBase.setCode(code);
                    stockBase.setName(name);
                    stockBase.setTheme(theme);
                    stockBase.setIndustry(industry);
                    stockBase.setNameAbbr(getNameAbbr(name));
                    try {
                        stockBaseMapper.insert(stockBase);
                    } catch (Exception e) {
                        try {
                            stockBaseMapper.updateByPrimaryKeySelective(stockBase);
                        }catch (Exception e1){
                            log.error("更新"+name+"失败");
                        }
                    }
                }
            }


    }


    /**
     * 获取名称缩写
     * @param name
     * @return
     */
    private String getNameAbbr(String name) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < name.length(); i++) {
            String trans = null;
            try {
                trans = TongHuaShunUtil.getTrans(name.substring(i, i + 1));
            } catch (Exception e) {
                return "";
            }
            sb.append(trans.trim().substring(0, 1));
        }
        String replace = sb.toString().replace("ā", "a");

        return replace;
    }


    public Object findAll(StockBaseDTO dto) {
        PageHelper.startPage(1, 200);
        if (StringUtils.isNotBlank(dto.getParamName())) {
            return stockBaseMapper.selectByCodeLike(dto.getParamName());
        } else {
            return stockBaseMapper.selectByAll(null);
        }

    }


    public void addConvertBondProcess() {

        List<StockBaseDetail> convertBond = dongFangSortService.getAllConvertBond();
        for(StockBaseDetail stockBaseDetail:convertBond){
            StockBase stockBase = new StockBase();
            stockBase.setCode(stockBaseDetail.getCode());
            stockBase.setName(stockBaseDetail.getName());
            stockBase.setNameAbbr(getNameAbbr(stockBaseDetail.getName()));
            if(StringUtils.isNotBlank(stockBaseDetail.getConvertCode())){
                StockBase stockBase1 = stockBaseMapper.selectByPrimaryKey(stockBaseDetail.getConvertCode());
                if(stockBase1!=null){
                    stockBase.setIndustry(stockBase1.getIndustry());
                    stockBase.setTheme(stockBase1.getTheme());
                }
            }

            try {
                stockBaseMapper.insert(stockBase);
            } catch (Exception e) {
                try {
                    stockBaseMapper.updateByPrimaryKeySelective(stockBase);
                }catch (Exception e1){
                    log.error("更新"+stockBaseDetail.getName()+"失败");
                }
            }
        }
    }
}
