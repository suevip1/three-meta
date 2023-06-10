package com.coatardbul.baseService.service;

import com.coatardbul.baseService.entity.bo.TickInfo;

import java.util.List;
import java.util.Map;

public interface DataServiceBridge {


    /**
     * 刷新基本信息
     *
     * @param code
     */
    void getAndRefreshStockInfo(String code);

    /**
     * 查看历史信息
     * @param code
     * @param dateFormat
     */
    void getAndRefreshStockInfo(String code,String dateFormat);

    String getStockInfo(String code);

    String getStockInfo(String code,Boolean proxyFlag);


    void rebuildStockDetailMap(String response, Map map);

    /**
     * tick数据
     *
     * @param code
     */

    void refreshStockTickInfo(String code);



    String getStockTickInfo(String code);
    List<TickInfo> getStockTickDetail(String code, String response);


     void updateTickInfoToStockInfo(List<TickInfo> list, Map newStockDetailMap);
        /**
         * 分钟数据
         * @param code
         */

    void refreshStockMinuterInfo(String code);



    void refreshStockMinuterInfo(String code,String dateFormat);

    String getStockMinuterInfo(String code);

    String getStockMinuterInfo(String code,String dateFormat);

    List getStockMinuterDetail(String code, String response);

    List getStockMinuterDetail(String code,String dateformat, String response);


}
