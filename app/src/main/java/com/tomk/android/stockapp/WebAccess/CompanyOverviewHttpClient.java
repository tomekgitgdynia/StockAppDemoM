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
 *
 * url = 'https://www.alphavantage.co/query?function=OVERVIEW&symbol=IBM&apikey=demo'
 *
 */
public class CompanyOverviewHttpClient {

    public static final int GOOD_CONNECTION = 200;

    public String getCompanyOverview(String stockSymbol, String apiKey) {

        String returnErrorMessage = MainActivity.NO_ERRORS;

        OkHttpClient httpClient = new OkHttpClient();
        Retrofit retrofit = new Retrofit.Builder().baseUrl("https://www.alphavantage.co").client(httpClient).addConverterFactory(GsonConverterFactory.create()).build();

        StockDataApiInterface apiService = retrofit.create(StockDataApiInterface.class);
        Call<ResponseBody> call = apiService.getCompanyOverviewData(stockSymbol,apiKey);
        retrofit2.Response<ResponseBody> response = null;
        String responseBodyString = null;
        try {
            response = call.execute();
            if (response.code() == GOOD_CONNECTION) {
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
