package com.coatardbul.sail.service.stockData;

public interface DataServiceBridge {


    void getAndRefreshStockInfo(String code);

    void refreshStockTickInfo(String code);

    void refreshStockMinuterInfo(String code);
}
