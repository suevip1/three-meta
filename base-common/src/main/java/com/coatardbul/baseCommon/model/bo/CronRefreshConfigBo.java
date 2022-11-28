package com.coatardbul.baseCommon.model.bo;

import lombok.Data;

/**
 * <p>
 * Note:
 * <p>
 * Date: 2022/11/11
 *
 * @author Su Xiaolei
 */
@Data
public class CronRefreshConfigBo {


    /**
     * 新浪财经是否开启代理
     */
    private Boolean isProxy;

    /**
     * code存在时间，小时
     */
    private  Integer codeExistHour ;

    /**
     * 定时任务是否开启定时刷新标识
     */
    private Boolean isOpenCronRefreshFlag;


    /**
     * 数据来源
     * @see com.coatardbul.stock.common.constants.DataSourceEnum
     */
    private String  dataSource;

    /**
     * socket  超时时间，单位毫秒，默认5000
     */
    private Integer  sockTimeout;

    /**
     * 每批次传送数据，默认是5个
     */
    private Integer batchNum;

}
