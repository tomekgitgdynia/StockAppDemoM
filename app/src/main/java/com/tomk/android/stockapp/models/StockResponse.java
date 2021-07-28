package com.tomk.android.stockapp.models;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class StockResponse
{
    private MetaData metaData;
    private ArrayList<TimeSeriesItem> timeSeriesItems;

    private String resultString;

    public static final int GRAPH_LOW = 1;
    public static final int GRAPH_HIGH = 2;

    public static final int GRAPH_OPEN = 3;
    public static final int GRAPH_CLOSE = 4;

    public static final int GRAPH_VOLUME = 5;

    private LinkedHashMap<String, String> companyOverviewMap = new LinkedHashMap<>();

    /**
     *
     */
    public StockResponse(MetaData metaData, ArrayList<TimeSeriesItem> timeSeriesItems) {
        super();
        this.metaData = metaData;
        this.timeSeriesItems = timeSeriesItems;
    }

    public StockResponse() {

    }


    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public ArrayList<TimeSeriesItem> getTimeSeriesItems() {
        return timeSeriesItems;
    }

    public void setTimeSeriesItems(ArrayList<TimeSeriesItem> timeSeriesItems) {
        this.timeSeriesItems = timeSeriesItems;
    }

    public LinkedHashMap<String, String> getCompanyOverviewMap() {
        return companyOverviewMap;
    }

    public void setCompanyOverviewMap(LinkedHashMap<String, String> companyOverviewMap) {
        this.companyOverviewMap = companyOverviewMap;
    }

    public String getResultString() {
        return resultString;
    }

    public void setResultString(String resultString) {
        this.resultString = resultString;
    }
}