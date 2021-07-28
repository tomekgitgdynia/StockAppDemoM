package com.tomk.android.stockapp.WebAccess;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface StockDataApiInterface {

    @GET("/query?function=TIME_SERIES_INTRADAY")
    Call<ResponseBody> getStockData( @Query("symbol") String symbol,
                                     @Query("interval") String interval,
                                     @Query("outputsize") String outputsize,
                                     @Query("apikey") String apikey);

    @GET("/query?function=OVERVIEW")
    Call<ResponseBody> getCompanyOverviewData( @Query("symbol") String symbol,
                                               @Query("apikey") String apikey);

}