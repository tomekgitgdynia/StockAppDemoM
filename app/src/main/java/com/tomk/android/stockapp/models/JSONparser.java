package com.tomk.android.stockapp.models;

import android.util.Log;

import com.tomk.android.stockapp.MainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class JSONparser {

    private static final String TAG = "JSONparser";

    public static StockResponse getStockResponse(String rawData) throws JSONException, ParseException {

        // Basic validation
        if (rawData == null || rawData.length() < 1) {
            return null;
        } else
        {
            return packStockResponseFromRaw(rawData);
        }
    }

    private static StockResponse packStockResponseFromRaw(String rawData) throws JSONException, ParseException {

        JSONObject jObj = new JSONObject(rawData);
        JSONObject metaDataJsonObj = getObject("Meta Data", jObj);

        // Pack Meta Data
        MetaData md = null;
        if (metaDataJsonObj == null) {
            Log.d(TAG, "metaDataJsonObj is null");

        } else {
            String lastRefreshString = metaDataJsonObj.getString("3. Last Refreshed");
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
            Date lastRefreshDate = null;
            String intervalString = metaDataJsonObj.getString("4. Interval").replaceAll("min", "");
            Integer intervalInteger = null;

            lastRefreshDate = fmt.parse(lastRefreshString);
            intervalInteger = Integer.parseInt(intervalString);
            md = new MetaData(metaDataJsonObj.getString("1. Information"), metaDataJsonObj.getString("2. Symbol"), lastRefreshDate, intervalInteger, metaDataJsonObj.getString("5. Output Size"), metaDataJsonObj.getString("6. Time Zone"));
        }

        StockResponse stockResponse = new StockResponse();
        stockResponse.setMetaData(md);

        // Pack time Series
        String intervalStringWithUnit = (String) metaDataJsonObj.get("4. Interval");
        String timeSeriesKey = "Time Series " + "(" + intervalStringWithUnit + ")";
        JSONObject timeSeries = getObject(timeSeriesKey, jObj);
        Iterator<String> tsKeys = timeSeries.keys();
        ArrayList<TimeSeriesItem> timeSeriesItems = new ArrayList<>();
        Date intervalDate = null;
        int cnt = 0;

        String intervalString = intervalStringWithUnit.replaceAll("min", "");
        Integer intervalInteger = Integer.parseInt(intervalString);
        for (Iterator<String> tsIterator = tsKeys; tsIterator.hasNext(); ) {
            if (cnt >= MainActivity.DATA_POINTS_NUM) break;
            String tsKey = tsIterator.next();
            JSONObject timeSeriesJsonObjectItem = new JSONObject(timeSeries.get(tsKey).toString());

            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            intervalDate = fmt.parse(tsKey);
            cnt++;

            Double open = Double.valueOf(timeSeriesJsonObjectItem.get("1. open").toString());
            Double close = Double.valueOf(timeSeriesJsonObjectItem.get("4. close").toString());
            Double high = Double.valueOf(timeSeriesJsonObjectItem.get("2. high").toString());
            Double low = Double.valueOf(timeSeriesJsonObjectItem.get("3. low").toString());
            Integer volume = Integer.valueOf(timeSeriesJsonObjectItem.getString("5. volume"));

            TimeSeriesItem timeSeriesItem = new TimeSeriesItem(intervalDate, intervalInteger, open, close, high, low, volume);
            timeSeriesItems.add(0, timeSeriesItem);
        }

        stockResponse.setTimeSeriesItems(timeSeriesItems);

        return stockResponse;
    }


    private static JSONObject getObject(String tagName, JSONObject jObj) throws JSONException {
        return jObj.getJSONObject(tagName);
    }
}
