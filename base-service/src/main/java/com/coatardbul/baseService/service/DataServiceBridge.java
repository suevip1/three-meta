package com.coatardbul.baseService.service;

public interface DataServiceBridge {


    void getAndRefreshStockInfo(String code);

    void refreshStockTickInfo(String code);

    void refreshStockMinuterInfo(String code);
}
