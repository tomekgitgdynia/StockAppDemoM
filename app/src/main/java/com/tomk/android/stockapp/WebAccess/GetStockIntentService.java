package com.tomk.android.stockapp.WebAccess;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.google.gson.Gson;
import com.tomk.android.stockapp.MainActivity;
import com.tomk.android.stockapp.StockDbAdapter;
import com.tomk.android.stockapp.StockListItem;
import com.tomk.android.stockapp.Util;

import com.tomk.android.stockapp.models.JSONparser;
import com.tomk.android.stockapp.models.Repository.DataRepository;
import com.tomk.android.stockapp.models.Repository.RepositoryItem;
import com.tomk.android.stockapp.models.StockResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class GetStockIntentService extends IntentService {

    public static final String STOCK_LIST_ACTION = "com.tomk.android.stockapp.STOCK_ACTION";
    public static final String NUMBER_OF_ITEMS = "0";
    public static final String RESULT_STRING = "";
    private StockDbAdapter stockDbAdapter = null;


    public GetStockIntentService() {
        super("GetStockIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int numberOfItems = 0;

        Intent broadcastIntent = new Intent();
        StockResponse stockResponse = null;
        if (intent != null) {
            String type = intent.getStringExtra("type");

            String stockSymbol = intent.getStringExtra(MainActivity.STOCK_SYMBOL);
            String interval = intent.getStringExtra(MainActivity.INTERVAL);
            String outputSize = intent.getStringExtra(MainActivity.OUTPUT_SIZE);
            String seriesType = intent.getStringExtra(MainActivity.SERIES_TYPE);
            String apiKeyObtained = intent.getStringExtra(MainActivity.API_KEY_OBTAINED);

            stockResponse = handleActionStockResponse(stockSymbol, interval, outputSize, seriesType, apiKeyObtained, type);

            // Save the stock response to database
            if (stockResponse != null && stockResponse.getTimeSeriesItems() != null && stockResponse.getTimeSeriesItems().size() != 0) {
                if (stockResponse.getResultString() != null && stockResponse.getResultString().equals(MainActivity.NO_ERRORS)) {
                    saveDataInDB(stockResponse);
                    System.out.println(" 777777777777777777777777 onHandleIntent saveDataInDB " + stockResponse.getCompanyOverviewMap().get("symbol"));
                }
                numberOfItems = stockResponse.getTimeSeriesItems().size();
            }
        }

        // After the downloaded items have been saved in database, the event is broadcast to the Activity
        broadcastIntent.putExtra(NUMBER_OF_ITEMS, String.valueOf(numberOfItems));
        broadcastIntent.putExtra(RESULT_STRING, stockResponse.getResultString());
        broadcastIntent.setAction(STOCK_LIST_ACTION);
        sendBroadcast(broadcastIntent);
    }

    /**
     * After the Stock Data has been downloaded from the Web Services, it is then
     * parsed and packed into a list of items and returned.
     */
    private StockResponse handleActionStockResponse(String stockSymbol, String interval, String timePeriod, String seriesType, String apiKeyObtained, String type) {


        StockResponse stockResponse = new StockResponse();
        String rawData = null;
        ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Util.isWiFiAvailable(conMan)) {

            StockSymbolHttpClient stockSymbolHttpClient = new StockSymbolHttpClient();
            rawData = stockSymbolHttpClient.getStockData(stockSymbol, interval, timePeriod, seriesType, apiKeyObtained, type);

            if (rawData == null || rawData.length() < 1 ) {
                stockResponse.setResultString(MainActivity.ERROR_CONNECTING);
                return stockResponse;
            } else if (rawData.contains(MainActivity.STOCK_NOT_FOUND)) {
                stockResponse.setResultString(MainActivity.STOCK_NOT_FOUND);
                return stockResponse;
            } else if (rawData.contains(MainActivity.ERROR_CONNECTING)) {
                stockResponse.setResultString(MainActivity.ERROR_CONNECTING);
                return stockResponse;
            }

            try {
                if(stockResponse != null)
                {
                    stockResponse = JSONparser.getStockResponse(rawData);
                } else
                {
                    stockResponse = new StockResponse();
                    stockResponse.setResultString(MainActivity.STOCK_NOT_FOUND);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            // If we got to this point there are no errors so we set the result string according;y
            stockResponse.setResultString(MainActivity.NO_ERRORS);
        } else {
            stockResponse.setResultString(MainActivity.NO_WIFI);
            return stockResponse;
        }


        // Now try to get the Company Overview raw string from the web services

        String rawDataCompanyOverview = null;
        conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (Util.isWiFiAvailable(conMan)) {

            //Use http client to obtain data from the web services call.  Raw data is returned as a Stringf
            CompanyOverviewHttpClient companyOverviewHttpClient = new CompanyOverviewHttpClient();
            rawDataCompanyOverview = companyOverviewHttpClient.getCompanyOverview(stockSymbol,apiKeyObtained);

            // Validate the raw data string
            if (rawData == null || rawData.length() < 1 ) {
                stockResponse.setResultString(MainActivity.ERROR_CONNECTING);
                return stockResponse;
            } else if (rawData.contains(MainActivity.STOCK_NOT_FOUND)) {
                stockResponse.setResultString(MainActivity.STOCK_NOT_FOUND);
                return stockResponse;
            } else if (rawData.contains(MainActivity.ERROR_CONNECTING)) {
                stockResponse.setResultString(MainActivity.ERROR_CONNECTING);
                return stockResponse;
            }

            // Convert from raw string to JSON
            Gson gson = new Gson();
            CompanyOverview companyOverview  = gson.fromJson(rawDataCompanyOverview, CompanyOverview.class);
            stockResponse.setResultString(MainActivity.NO_ERRORS);

            LinkedHashMap companyOverviewMap = createCompanyOverviewMap(companyOverview);
            stockResponse.setCompanyOverviewMap(companyOverviewMap);

        } else {
            stockResponse.setResultString(MainActivity.NO_WIFI);
            return stockResponse;
        }

        return stockResponse;
    }

    public static LinkedHashMap createCompanyOverviewMap(CompanyOverview companyOverview)  {

        LinkedHashMap companyOverviewMap = new LinkedHashMap<String, String>();

        companyOverviewMap.put("symbol", companyOverview.getSymbol());
        companyOverviewMap.put("assetType", companyOverview.getAssetType());
        companyOverviewMap.put("description", companyOverview.getDescription());
        companyOverviewMap.put("cik", companyOverview.getCik());
        companyOverviewMap.put("exchange", companyOverview.getExchange());
        companyOverviewMap.put("currency", companyOverview.getCurrency());
        companyOverviewMap.put("country", companyOverview.getCountry());
        companyOverviewMap.put("sector", companyOverview.getSector());
        companyOverviewMap.put("industry", companyOverview.getIndustry());
        companyOverviewMap.put("address", companyOverview.getAddress());
        companyOverviewMap.put("fiscalYearEnd", companyOverview.getFiscalYearEnd());
        companyOverviewMap.put("latestQuarter", companyOverview.getLatestQuarter());
        companyOverviewMap.put("marketCapitalization", companyOverview.getMarketCapitalization());
        companyOverviewMap.put("ebitda", companyOverview.getEbitda());
        companyOverviewMap.put("pERatio", companyOverview.getPERatio());
        companyOverviewMap.put("pEGRatio", companyOverview.getPEGRatio());
        companyOverviewMap.put("bookValue", companyOverview.getBookValue());
        companyOverviewMap.put("dividendPerShare", companyOverview.getDividendPerShare());
        companyOverviewMap.put("dividendYield", companyOverview.getDividendYield());
        companyOverviewMap.put("eps", companyOverview.getEps());
        companyOverviewMap.put("revenuePerShareTTM", companyOverview.getRevenuePerShareTTM());
        companyOverviewMap.put("profitMargin", companyOverview.getProfitMargin());
        companyOverviewMap.put("operatingMarginTTM", companyOverview.getOperatingMarginTTM());
        companyOverviewMap.put("returnOnAssetsTTM", companyOverview.getReturnOnAssetsTTM());
        companyOverviewMap.put("returnOnEquityTTM", companyOverview.getReturnOnEquityTTM());
        companyOverviewMap.put("revenueTTM", companyOverview.getRevenueTTM());
        companyOverviewMap.put("grossProfitTTM", companyOverview.getGrossProfitTTM());
        companyOverviewMap.put("dilutedEPSTTM", companyOverview.getDilutedEPSTTM());
        companyOverviewMap.put("quarterlyEarningsGrowthYOY", companyOverview.getQuarterlyEarningsGrowthYOY());
        companyOverviewMap.put("quarterlyRevenueGrowthYOY", companyOverview.getQuarterlyRevenueGrowthYOY());
        companyOverviewMap.put("analystTargetPrice", companyOverview.getAnalystTargetPrice());
        companyOverviewMap.put("trailingPE", companyOverview.getTrailingPE());
        companyOverviewMap.put("forwardPE", companyOverview.getForwardPE());
        companyOverviewMap.put("priceToSalesRatioTTM", companyOverview.getPriceToSalesRatioTTM());
        companyOverviewMap.put("priceToBookRatio", companyOverview.getPriceToBookRatio());
        companyOverviewMap.put("eVToRevenue", companyOverview.getEVToRevenue());
        companyOverviewMap.put("eVToEBITDA", companyOverview.getEVToEBITDA());
        companyOverviewMap.put("beta", companyOverview.getBeta());
        companyOverviewMap.put("_52WeekHigh", companyOverview.get52WeekHigh());
        companyOverviewMap.put("_52WeekLow", companyOverview.get52WeekLow());
        companyOverviewMap.put("_50DayMovingAverage", companyOverview.get50DayMovingAverage());
        companyOverviewMap.put("_200DayMovingAverage", companyOverview.get200DayMovingAverage());
        companyOverviewMap.put("sharesOutstanding", companyOverview.getSharesOutstanding());
        companyOverviewMap.put("sharesFloat", companyOverview.getSharesFloat());
        companyOverviewMap.put("sharesShort", companyOverview.getSharesShort());
        companyOverviewMap.put("sharesShortPriorMonth", companyOverview.getSharesShortPriorMonth());
        companyOverviewMap.put("shortRatio", companyOverview.getShortRatio());
        companyOverviewMap.put("shortPercentOutstanding", companyOverview.getShortPercentOutstanding());
        companyOverviewMap.put("shortPercentFloat", companyOverview.getShortPercentFloat());
        companyOverviewMap.put("percentInsiders", companyOverview.getPercentInsiders());
        companyOverviewMap.put("percentInstitutions", companyOverview.getPercentInstitutions());
        companyOverviewMap.put("forwardAnnualDividendRate", companyOverview.getForwardAnnualDividendRate());
        companyOverviewMap.put("forwardAnnualDividendYield", companyOverview.getForwardAnnualDividendYield());
        companyOverviewMap.put("payoutRatio", companyOverview.getPayoutRatio());
        companyOverviewMap.put("dividendDate", companyOverview.getDividendDate());
        companyOverviewMap.put("exDividendDate", companyOverview.getExDividendDate());
        companyOverviewMap.put("lastSplitFactor", companyOverview.getLastSplitFactor());
        companyOverviewMap.put("lastSplitDate", companyOverview.getLastSplitDate());


        return companyOverviewMap;
    }


    // Call adapter methods to save data to db
    protected void saveDataInDB(StockResponse stockResponse) {

        // Create database adapter and open the database
        if (stockDbAdapter == null) {
            stockDbAdapter = new StockDbAdapter(this.getApplicationContext());
            stockDbAdapter.open();
        }

        stockDbAdapter.insertStockResponse(stockResponse);

        DataRepository repository = new DataRepository();
        ArrayList<StockListItem> stocksListItems = new ArrayList<>();

        // Handle the list of stocks and save it in the database
        for (RepositoryItem repositoryItem : repository.getRepository()) {
            StockListItem stockListItem = new StockListItem(repositoryItem.getStockSymbol(), repositoryItem.getStockName(), null, null, null);
            stocksListItems.add(stockListItem);
        }

        stockDbAdapter.insertStocksList(stocksListItems, true);

    }


}
