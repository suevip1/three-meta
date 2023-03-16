package com.coatardbul.stock.service.statistic;

import com.alibaba.fastjson.JSONObject;
import com.coatardbul.baseService.service.DongFangCommonService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2023/3/15
 *
 * @author Su Xiaolei
 */
@Service
@Slf4j
public class DongFangAbnormalMovementService {


    @Autowired
    DongFangCommonService dongFangCommonService;


    public Object getStockAbnormalMovement(String code, String name, String dateFormat) {

        String stockAbnormalMovement = dongFangCommonService.getStockAbnormalMovement(code, dateFormat);

        JSONObject data=null;
        if (StringUtils.isNotBlank(stockAbnormalMovement)) {
            int beginIndex = stockAbnormalMovement.indexOf("(");
            int endIndex = stockAbnormalMovement.lastIndexOf(")");
            stockAbnormalMovement = stockAbnormalMovement.substring(beginIndex + 1, endIndex);
            JSONObject jsonObject = JSONObject.parseObject(stockAbnormalMovement);
             data = jsonObject.getJSONObject("data");
        }
        if(data==null){
            Map<String, Object> map = new HashMap<>();
            map.put("c", code);
            map.put("n", name);
            return map;
        }else {
            return data;
        }
    }
}
