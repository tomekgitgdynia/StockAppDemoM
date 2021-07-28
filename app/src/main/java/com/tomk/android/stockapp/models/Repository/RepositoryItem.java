package com.tomk.android.stockapp.models.Repository;

public class RepositoryItem {

    private String stockSymbol;
    private String stockName;

    RepositoryItem (String symbol, String name)
    {
        stockSymbol = symbol;
        stockName = name;
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
}
