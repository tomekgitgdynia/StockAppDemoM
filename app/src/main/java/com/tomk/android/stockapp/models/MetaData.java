package com.tomk.android.stockapp.models;

import java.util.Date;

/**
 * Created by a on 8/20/2018.
 //  "1. Information": "Intraday (5min) open, high, low, close prices and volume",
 //  "2. Symbol": "MSFT",
 //  "3. Last Refreshed": "2018-08-20 11:30:00",
 //  "4. Interval": "5min",
 //  "5. Output Size": "Full size",
 //  "6. Time Zone": "US/Eastern"
 */

public class MetaData {


    private String information = null;
    private String symbol = null;
    private Date lastRefreshedDate = null;
    private Integer interval = null;
    private String outputSize = null;
    private String timeZone = null;

    public MetaData()
    {

    }
    public MetaData(String information, String symbol, Date lastRefreshedDate, Integer interval, String outputSize, String timeZone) {
        this.information = information;
        this.symbol = symbol;
        this.lastRefreshedDate = lastRefreshedDate;
        this.interval = interval;
        this.outputSize = outputSize;
        this.timeZone = timeZone;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Date getLastRefreshedDate() {
        return lastRefreshedDate;
    }

    public void setLastRefreshedDate(Date lastRefreshedDate) {
        this.lastRefreshedDate = lastRefreshedDate;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public String getOutputSize() {
        return outputSize;
    }

    public void setOutputSize(String outputSize) {
        this.outputSize = outputSize;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }
}
