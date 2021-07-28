package com.tomk.android.stockapp.models;

import java.util.Date;

/**
 * Created by a on 8/20/2018.
 */

public class TimeSeriesItem {

    private Date date;
    private Integer interval;
    private Double open;
    private Double close;
    private Double high;
    private Double low;
    private Integer volume;

    public TimeSeriesItem() {
    }

    public TimeSeriesItem(Date date, Integer interval, Double open, Double close, Double high, Double low, Integer volume) {


        this.date=date;
        this.interval=interval;
        this.open=open;
        this.close=close;
        this.high=high;
        this.low=low;
        this.volume=volume;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Double getOpen() {
        return open;
    }

    public void setOpen(Double open) {
        this.open = open;
    }

    public Double getClose() {
        return close;
    }

    public void setClose(Double close) {
        this.close = close;
    }

    public Double getHigh() {
        return high;
    }

    public void setHigh(Double high) {
        this.high = high;
    }

    public Double getLow() {
        return low;
    }

    public void setLow(Double low) {
        this.low = low;
    }

    public Integer getVolume() {
        return volume;
    }

    public void setVolume(Integer volume) {
        this.volume = volume;
    }


}
