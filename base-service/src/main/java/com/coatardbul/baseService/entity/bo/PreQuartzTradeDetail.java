package com.coatardbul.baseService.entity.bo;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/12/8
 *
 * @author Su Xiaolei
 */
@Data
public class PreQuartzTradeDetail extends PreTradeDetail{

    private Boolean tradeFlag=false;

    /**
     * 定时任务的标识
     */
    private String quartzSign;

}
