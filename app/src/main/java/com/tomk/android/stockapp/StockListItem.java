package com.tomk.android.stockapp;


/**
 * Created by Tom Kowszun.
 */

public class StockListItem {

    private String stockSymbol;
    private String stockName;
    private String stockDescription;
    private String marketName;
    private String marketDescription;

    public StockListItem() {
        stockSymbol = "";
        stockName = "";
    }

    public StockListItem(String stockSymbol, String stockName, String stockDescription, String marketName, String marketDescription) {
        this.stockSymbol = stockSymbol;
        this.stockName = stockName;
        this.stockDescription = stockDescription;
        this.marketName = marketName;
        this.marketDescription = marketDescription;
    }
    public String getStockSymbol() {
        return stockSymbol;
    }

    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getStockDescription() {
        return stockDescription;
    }

    public void setStockDescription(String stockDescription) {
        this.stockDescription = stockDescription;
    }

    public String getMarketName() {
        return marketName;
    }

    public void setMarketName(String marketName) {
        this.marketName = marketName;
    }

    public String getMarketDescription() {
        return marketDescription;
    }

    public void setMarketDescription(String marketDescription) {
        this.marketDescription = marketDescription;
    }
}
