package com.tomk.android.stockapp.WebAccess;

import android.util.Log;

import com.tomk.android.stockapp.MainActivity;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by Tom Kowszun on 11/10/2017.
 * <p>
 */
public class StockSymbolHttpClient {


    public static final int GOOD_CONNECTION = 200;

    public String getStockData(String stockSymbol, String interval, String outputSize, String seriesType, String apiKey, String requestedType) {

        outputSize = MainActivity.OUTPUT_SIZE_VAL;
        String queryString = null;

        return getStockDataForQueryString(stockSymbol, interval, outputSize, seriesType, apiKey, requestedType);
    }


    private String getStockDataForQueryString(String stockSymbol, String interval, String outputSize, String seriesType, String apiKey, String requestedType) {
        InputStream is = null;
        String returnErrorMessage = MainActivity.NO_ERRORS;

        OkHttpClient httpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.alphavantage.co").client(httpClient).addConverterFactory(GsonConverterFactory.create()).build();

        StockDataApiInterface apiService = retrofit.create(StockDataApiInterface.class);
        Call<ResponseBody> call = apiService.getStockData(stockSymbol, interval, outputSize, apiKey);
        retrofit2.Response<ResponseBody> response = null;
        String responseBodyString = null;
        try {
            response = call.execute();
            if (response.code() == GOOD_CONNECTION) {
                ResponseBody rb = response.body();
                responseBodyString = response.body().string();
                return responseBodyString;
            } else {
                return returnErrorMessage;
            }

        } catch (IOException ioex) {
            Log.e("DEBUG", "Network call failed ");
            return returnErrorMessage;
        }

    }


}
