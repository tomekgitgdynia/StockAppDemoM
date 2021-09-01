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

//            rawData = rawDataTest; // testing only
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

String rawDataTest = "{\n" + "    \"Meta Data\": {\n" + "        \"1. Information\": \"Intraday (60min) open, high, low, close prices and volume\",\n" + "        \"2. Symbol\": \"NDAQ\",\n" + "        \"3. Last Refreshed\": \"2021-08-09 17:00:00\",\n" + "        \"4. Interval\": \"60min\",\n" + "        \"5. Output Size\": \"Compact\",\n" + "        \"6. Time Zone\": \"US/Eastern\"\n" + "    },\n" + "    \"Time Series (60min)\": {\n" + "        \"2021-08-09 17:00:00\": {\n" + "            \"1. open\": \"188.6800\",\n" + "            \"2. high\": \"188.6800\",\n" + "            \"3. low\": \"188.6800\",\n" + "            \"4. close\": \"188.6800\",\n" + "            \"5. volume\": \"70577\"\n" + "        },\n" + "        \"2021-08-09 16:00:00\": {\n" + "            \"1. open\": \"188.5700\",\n" + "            \"2. high\": \"188.8500\",\n" + "            \"3. low\": \"188.3300\",\n" + "            \"4. close\": \"188.6300\",\n" + "            \"5. volume\": \"121768\"\n" + "        },\n" + "        \"2021-08-09 15:00:00\": {\n" + "            \"1. open\": \"188.4900\",\n" + "            \"2. high\": \"188.6890\",\n" + "            \"3. low\": \"188.2300\",\n" + "            \"4. close\": \"188.5800\",\n" + "            \"5. volume\": \"53490\"\n" + "        },\n" + "        \"2021-08-09 14:00:00\": {\n" + "            \"1. open\": \"188.4150\",\n" + "            \"2. high\": \"188.6050\",\n" + "            \"3. low\": \"188.2500\",\n" + "            \"4. close\": \"188.4950\",\n" + "            \"5. volume\": \"26059\"\n" + "        },\n" + "        \"2021-08-09 13:00:00\": {\n" + "            \"1. open\": \"188.5300\",\n" + "            \"2. high\": \"188.6800\",\n" + "            \"3. low\": \"188.1800\",\n" + "            \"4. close\": \"188.3700\",\n" + "            \"5. volume\": \"34180\"\n" + "        },\n" + "        \"2021-08-09 12:00:00\": {\n" + "            \"1. open\": \"188.6800\",\n" + "            \"2. high\": \"188.7350\",\n" + "            \"3. low\": \"188.0100\",\n" + "            \"4. close\": \"188.5600\",\n" + "            \"5. volume\": \"43669\"\n" + "        },\n" + "        \"2021-08-09 11:00:00\": {\n" + "            \"1. open\": \"188.0700\",\n" + "            \"2. high\": \"188.9400\",\n" + "            \"3. low\": \"187.8600\",\n" + "            \"4. close\": \"188.6900\",\n" + "            \"5. volume\": \"48317\"\n" + "        },\n" + "        \"2021-08-09 10:00:00\": {\n" + "            \"1. open\": \"188.8200\",\n" + "            \"2. high\": \"189.0300\",\n" + "            \"3. low\": \"188.0000\",\n" + "            \"4. close\": \"188.2300\",\n" + "            \"5. volume\": \"63100\"\n" + "        },\n" + "        \"2021-08-09 05:00:00\": {\n" + "            \"1. open\": \"192.4000\",\n" + "            \"2. high\": \"192.4000\",\n" + "            \"3. low\": \"192.4000\",\n" + "            \"4. close\": \"192.4000\",\n" + "            \"5. volume\": \"156\"\n" + "        },\n" + "        \"2021-08-06 17:00:00\": {\n" + "            \"1. open\": \"188.7200\",\n" + "            \"2. high\": \"188.7200\",\n" + "            \"3. low\": \"188.7200\",\n" + "            \"4. close\": \"188.7200\",\n" + "            \"5. volume\": \"1876\"\n" + "        },\n" + "        \"2021-08-06 16:00:00\": {\n" + "            \"1. open\": \"188.2900\",\n" + "            \"2. high\": \"188.9500\",\n" + "            \"3. low\": \"188.2900\",\n" + "            \"4. close\": \"188.8800\",\n" + "            \"5. volume\": \"117423\"\n" + "        },\n" + "        \"2021-08-06 15:00:00\": {\n" + "            \"1. open\": \"188.6500\",\n" + "            \"2. high\": \"188.7800\",\n" + "            \"3. low\": \"188.3100\",\n" + "            \"4. close\": \"188.3100\",\n" + "            \"5. volume\": \"53503\"\n" + "        },\n" + "        \"2021-08-06 14:00:00\": {\n" + "            \"1. open\": \"188.7600\",\n" + "            \"2. high\": \"188.8500\",\n" + "            \"3. low\": \"188.4900\",\n" + "            \"4. close\": \"188.6600\",\n" + "            \"5. volume\": \"44227\"\n" + "        },\n" + "        \"2021-08-06 13:00:00\": {\n" + "            \"1. open\": \"188.0100\",\n" + "            \"2. high\": \"188.8100\",\n" + "            \"3. low\": \"187.9700\",\n" + "            \"4. close\": \"188.7400\",\n" + "            \"5. volume\": \"46144\"\n" + "        },\n" + "        \"2021-08-06 12:00:00\": {\n" + "            \"1. open\": \"187.9000\",\n" + "            \"2. high\": \"188.3100\",\n" + "            \"3. low\": \"187.7100\",\n" + "            \"4. close\": \"188.0200\",\n" + "            \"5. volume\": \"57844\"\n" + "        },\n" + "        \"2021-08-06 11:00:00\": {\n" + "            \"1. open\": \"188.5000\",\n" + "            \"2. high\": \"189.2050\",\n" + "            \"3. low\": \"187.5600\",\n" + "            \"4. close\": \"187.8950\",\n" + "            \"5. volume\": \"63391\"\n" + "        },\n" + "        \"2021-08-06 10:00:00\": {\n" + "            \"1. open\": \"189.3400\",\n" + "            \"2. high\": \"190.3050\",\n" + "            \"3. low\": \"188.4200\",\n" + "            \"4. close\": \"188.5200\",\n" + "            \"5. volume\": \"64406\"\n" + "        },\n" + "        \"2021-08-05 17:00:00\": {\n" + "            \"1. open\": \"188.6800\",\n" + "            \"2. high\": \"188.6800\",\n" + "            \"3. low\": \"188.6800\",\n" + "            \"4. close\": \"188.6800\",\n" + "            \"5. volume\": \"7892\"\n" + "        },\n" + "        \"2021-08-05 16:00:00\": {\n" + "            \"1. open\": \"187.8100\",\n" + "            \"2. high\": \"188.7700\",\n" + "            \"3. low\": \"187.8100\",\n" + "            \"4. close\": \"188.7000\",\n" + "            \"5. volume\": \"195155\"\n" + "        },\n" + "        \"2021-08-05 15:00:00\": {\n" + "            \"1. open\": \"187.8900\",\n" + "            \"2. high\": \"188.0192\",\n" + "            \"3. low\": \"187.6500\",\n" + "            \"4. close\": \"187.8400\",\n" + "            \"5. volume\": \"52903\"\n" + "        },\n" + "        \"2021-08-05 14:00:00\": {\n" + "            \"1. open\": \"187.7200\",\n" + "            \"2. high\": \"188.0000\",\n" + "            \"3. low\": \"187.5100\",\n" + "            \"4. close\": \"187.9100\",\n" + "            \"5. volume\": \"41163\"\n" + "        },\n" + "        \"2021-08-05 13:00:00\": {\n" + "            \"1. open\": \"187.8600\",\n" + "            \"2. high\": \"188.2300\",\n" + "            \"3. low\": \"187.7200\",\n" + "            \"4. close\": \"187.7300\",\n" + "            \"5. volume\": \"52833\"\n" + "        },\n" + "        \"2021-08-05 12:00:00\": {\n" + "            \"1. open\": \"188.8500\",\n" + "            \"2. high\": \"188.8500\",\n" + "            \"3. low\": \"187.7600\",\n" + "            \"4. close\": \"187.8600\",\n" + "            \"5. volume\": \"54427\"\n" + "        },\n" + "        \"2021-08-05 11:00:00\": {\n" + "            \"1. open\": \"189.8700\",\n" + "            \"2. high\": \"189.9100\",\n" + "            \"3. low\": \"188.7300\",\n" + "            \"4. close\": \"188.9050\",\n" + "            \"5. volume\": \"85891\"\n" + "        },\n" + "        \"2021-08-05 10:00:00\": {\n" + "            \"1. open\": \"190.0000\",\n" + "            \"2. high\": \"190.6900\",\n" + "            \"3. low\": \"189.6100\",\n" + "            \"4. close\": \"189.7800\",\n" + "            \"5. volume\": \"44207\"\n" + "        },\n" + "        \"2021-08-04 17:00:00\": {\n" + "            \"1. open\": \"189.3600\",\n" + "            \"2. high\": \"189.3600\",\n" + "            \"3. low\": \"189.3600\",\n" + "            \"4. close\": \"189.3600\",\n" + "            \"5. volume\": \"15181\"\n" + "        },\n" + "        \"2021-08-04 16:00:00\": {\n" + "            \"1. open\": \"189.9200\",\n" + "            \"2. high\": \"190.1600\",\n" + "            \"3. low\": \"189.3300\",\n" + "            \"4. close\": \"189.3600\",\n" + "            \"5. volume\": \"136391\"\n" + "        },\n" + "        \"2021-08-04 15:00:00\": {\n" + "            \"1. open\": \"189.9800\",\n" + "            \"2. high\": \"190.1700\",\n" + "            \"3. low\": \"189.8500\",\n" + "            \"4. close\": \"189.9250\",\n" + "            \"5. volume\": \"61605\"\n" + "        },\n" + "        \"2021-08-04 14:00:00\": {\n" + "            \"1. open\": \"190.0300\",\n" + "            \"2. high\": \"190.1850\",\n" + "            \"3. low\": \"189.8900\",\n" + "            \"4. close\": \"189.9800\",\n" + "            \"5. volume\": \"55781\"\n" + "        },\n" + "        \"2021-08-04 13:00:00\": {\n" + "            \"1. open\": \"189.6150\",\n" + "            \"2. high\": \"190.1200\",\n" + "            \"3. low\": \"189.6100\",\n" + "            \"4. close\": \"190.0300\",\n" + "            \"5. volume\": \"71130\"\n" + "        },\n" + "        \"2021-08-04 12:00:00\": {\n" + "            \"1. open\": \"189.9900\",\n" + "            \"2. high\": \"190.1900\",\n" + "            \"3. low\": \"189.3000\",\n" + "            \"4. close\": \"189.5300\",\n" + "            \"5. volume\": \"65884\"\n" + "        },\n" + "        \"2021-08-04 11:00:00\": {\n" + "            \"1. open\": \"189.0550\",\n" + "            \"2. high\": \"190.4500\",\n" + "            \"3. low\": \"188.9600\",\n" + "            \"4. close\": \"190.0200\",\n" + "            \"5. volume\": \"124669\"\n" + "        },\n" + "        \"2021-08-04 10:00:00\": {\n" + "            \"1. open\": \"188.5500\",\n" + "            \"2. high\": \"189.0750\",\n" + "            \"3. low\": \"187.6550\",\n" + "            \"4. close\": \"189.0700\",\n" + "            \"5. volume\": \"49101\"\n" + "        },\n" + "        \"2021-08-03 17:00:00\": {\n" + "            \"1. open\": \"188.6700\",\n" + "            \"2. high\": \"188.6700\",\n" + "            \"3. low\": \"188.6700\",\n" + "            \"4. close\": \"188.6700\",\n" + "            \"5. volume\": \"8136\"\n" + "        },\n" + "        \"2021-08-03 16:00:00\": {\n" + "            \"1. open\": \"188.0500\",\n" + "            \"2. high\": \"188.7900\",\n" + "            \"3. low\": \"188.0400\",\n" + "            \"4. close\": \"188.6700\",\n" + "            \"5. volume\": \"175956\"\n" + "        },\n" + "        \"2021-08-03 15:00:00\": {\n" + "            \"1. open\": \"187.9800\",\n" + "            \"2. high\": \"188.2700\",\n" + "            \"3. low\": \"187.8250\",\n" + "            \"4. close\": \"188.0500\",\n" + "            \"5. volume\": \"52008\"\n" + "        },\n" + "        \"2021-08-03 14:00:00\": {\n" + "            \"1. open\": \"187.9781\",\n" + "            \"2. high\": \"188.4400\",\n" + "            \"3. low\": \"187.8800\",\n" + "            \"4. close\": \"188.0000\",\n" + "            \"5. volume\": \"46081\"\n" + "        },\n" + "        \"2021-08-03 13:00:00\": {\n" + "            \"1. open\": \"188.1300\",\n" + "            \"2. high\": \"188.1970\",\n" + "            \"3. low\": \"187.7200\",\n" + "            \"4. close\": \"187.9350\",\n" + "            \"5. volume\": \"49379\"\n" + "        },\n" + "        \"2021-08-03 12:00:00\": {\n" + "            \"1. open\": \"189.0100\",\n" + "            \"2. high\": \"189.0100\",\n" + "            \"3. low\": \"187.8900\",\n" + "            \"4. close\": \"188.2050\",\n" + "            \"5. volume\": \"83702\"\n" + "        },\n" + "        \"2021-08-03 11:00:00\": {\n" + "            \"1. open\": \"188.0600\",\n" + "            \"2. high\": \"189.3800\",\n" + "            \"3. low\": \"187.9100\",\n" + "            \"4. close\": \"188.9900\",\n" + "            \"5. volume\": \"72700\"\n" + "        },\n" + "        \"2021-08-03 10:00:00\": {\n" + "            \"1. open\": \"189.1900\",\n" + "            \"2. high\": \"189.5400\",\n" + "            \"3. low\": \"187.9500\",\n" + "            \"4. close\": \"188.0600\",\n" + "            \"5. volume\": \"43881\"\n" + "        },\n" + "        \"2021-08-03 09:00:00\": {\n" + "            \"1. open\": \"189.5000\",\n" + "            \"2. high\": \"189.5000\",\n" + "            \"3. low\": \"189.5000\",\n" + "            \"4. close\": \"189.5000\",\n" + "            \"5. volume\": \"315\"\n" + "        },\n" + "        \"2021-08-03 07:00:00\": {\n" + "            \"1. open\": \"190.0000\",\n" + "            \"2. high\": \"190.0000\",\n" + "            \"3. low\": \"190.0000\",\n" + "            \"4. close\": \"190.0000\",\n" + "            \"5. volume\": \"114\"\n" + "        },\n" + "        \"2021-08-02 19:00:00\": {\n" + "            \"1. open\": \"188.4700\",\n" + "            \"2. high\": \"188.4700\",\n" + "            \"3. low\": \"188.4700\",\n" + "            \"4. close\": \"188.4700\",\n" + "            \"5. volume\": \"133\"\n" + "        },\n" + "        \"2021-08-02 17:00:00\": {\n" + "            \"1. open\": \"188.4300\",\n" + "            \"2. high\": \"188.4300\",\n" + "            \"3. low\": \"188.2500\",\n" + "            \"4. close\": \"188.2500\",\n" + "            \"5. volume\": \"4071\"\n" + "        },\n" + "        \"2021-08-02 16:00:00\": {\n" + "            \"1. open\": \"188.3200\",\n" + "            \"2. high\": \"188.4700\",\n" + "            \"3. low\": \"188.0400\",\n" + "            \"4. close\": \"188.4400\",\n" + "            \"5. volume\": \"139566\"\n" + "        },\n" + "        \"2021-08-02 15:00:00\": {\n" + "            \"1. open\": \"188.4100\",\n" + "            \"2. high\": \"188.6100\",\n" + "            \"3. low\": \"187.9700\",\n" + "            \"4. close\": \"188.3350\",\n" + "            \"5. volume\": \"40420\"\n" + "        },\n" + "        \"2021-08-02 14:00:00\": {\n" + "            \"1. open\": \"188.0550\",\n" + "            \"2. high\": \"188.5500\",\n" + "            \"3. low\": \"187.8600\",\n" + "            \"4. close\": \"188.4400\",\n" + "            \"5. volume\": \"31469\"\n" + "        },\n" + "        \"2021-08-02 13:00:00\": {\n" + "            \"1. open\": \"187.5700\",\n" + "            \"2. high\": \"187.9200\",\n" + "            \"3. low\": \"187.3479\",\n" + "            \"4. close\": \"187.9200\",\n" + "            \"5. volume\": \"34535\"\n" + "        },\n" + "        \"2021-08-02 12:00:00\": {\n" + "            \"1. open\": \"188.2900\",\n" + "            \"2. high\": \"188.9431\",\n" + "            \"3. low\": \"187.6700\",\n" + "            \"4. close\": \"187.6700\",\n" + "            \"5. volume\": \"43961\"\n" + "        },\n" + "        \"2021-08-02 11:00:00\": {\n" + "            \"1. open\": \"187.4400\",\n" + "            \"2. high\": \"188.2550\",\n" + "            \"3. low\": \"187.0300\",\n" + "            \"4. close\": \"188.2550\",\n" + "            \"5. volume\": \"69993\"\n" + "        },\n" + "        \"2021-08-02 10:00:00\": {\n" + "            \"1. open\": \"189.1900\",\n" + "            \"2. high\": \"189.1900\",\n" + "            \"3. low\": \"187.4200\",\n" + "            \"4. close\": \"187.4200\",\n" + "            \"5. volume\": \"48713\"\n" + "        },\n" + "        \"2021-08-02 06:00:00\": {\n" + "            \"1. open\": \"190.6000\",\n" + "            \"2. high\": \"190.6000\",\n" + "            \"3. low\": \"190.6000\",\n" + "            \"4. close\": \"190.6000\",\n" + "            \"5. volume\": \"100\"\n" + "        },\n" + "        \"2021-07-30 17:00:00\": {\n" + "            \"1. open\": \"186.7300\",\n" + "            \"2. high\": \"186.7300\",\n" + "            \"3. low\": \"186.7300\",\n" + "            \"4. close\": \"186.7300\",\n" + "            \"5. volume\": \"83474\"\n" + "        },\n" + "        \"2021-07-30 16:00:00\": {\n" + "            \"1. open\": \"187.0800\",\n" + "            \"2. high\": \"187.4300\",\n" + "            \"3. low\": \"186.5900\",\n" + "            \"4. close\": \"186.6100\",\n" + "            \"5. volume\": \"163151\"\n" + "        },\n" + "        \"2021-07-30 15:00:00\": {\n" + "            \"1. open\": \"187.5400\",\n" + "            \"2. high\": \"187.5400\",\n" + "            \"3. low\": \"186.6900\",\n" + "            \"4. close\": \"187.0500\",\n" + "            \"5. volume\": \"45642\"\n" + "        },\n" + "        \"2021-07-30 14:00:00\": {\n" + "            \"1. open\": \"187.4400\",\n" + "            \"2. high\": \"187.7600\",\n" + "            \"3. low\": \"187.3400\",\n" + "            \"4. close\": \"187.5400\",\n" + "            \"5. volume\": \"41304\"\n" + "        },\n" + "        \"2021-07-30 13:00:00\": {\n" + "            \"1. open\": \"187.2800\",\n" + "            \"2. high\": \"187.4850\",\n" + "            \"3. low\": \"187.0300\",\n" + "            \"4. close\": \"187.3800\",\n" + "            \"5. volume\": \"40352\"\n" + "        },\n" + "        \"2021-07-30 12:00:00\": {\n" + "            \"1. open\": \"187.5600\",\n" + "            \"2. high\": \"187.9700\",\n" + "            \"3. low\": \"187.0950\",\n" + "            \"4. close\": \"187.2900\",\n" + "            \"5. volume\": \"48189\"\n" + "        },\n" + "        \"2021-07-30 11:00:00\": {\n" + "            \"1. open\": \"187.5100\",\n" + "            \"2. high\": \"188.1500\",\n" + "            \"3. low\": \"187.1900\",\n" + "            \"4. close\": \"187.5500\",\n" + "            \"5. volume\": \"39309\"\n" + "        },\n" + "        \"2021-07-30 10:00:00\": {\n" + "            \"1. open\": \"186.6300\",\n" + "            \"2. high\": \"188.1300\",\n" + "            \"3. low\": \"185.7900\",\n" + "            \"4. close\": \"187.6700\",\n" + "            \"5. volume\": \"36321\"\n" + "        },\n" + "        \"2021-07-30 09:00:00\": {\n" + "            \"1. open\": \"186.0000\",\n" + "            \"2. high\": \"186.0000\",\n" + "            \"3. low\": \"186.0000\",\n" + "            \"4. close\": \"186.0000\",\n" + "            \"5. volume\": \"502\"\n" + "        },\n" + "        \"2021-07-29 17:00:00\": {\n" + "            \"1. open\": \"186.6800\",\n" + "            \"2. high\": \"186.6800\",\n" + "            \"3. low\": \"186.6800\",\n" + "            \"4. close\": \"186.6800\",\n" + "            \"5. volume\": \"11902\"\n" + "        },\n" + "        \"2021-07-29 16:00:00\": {\n" + "            \"1. open\": \"186.8500\",\n" + "            \"2. high\": \"187.0300\",\n" + "            \"3. low\": \"186.5800\",\n" + "            \"4. close\": \"186.6800\",\n" + "            \"5. volume\": \"98855\"\n" + "        },\n" + "        \"2021-07-29 15:00:00\": {\n" + "            \"1. open\": \"187.1700\",\n" + "            \"2. high\": \"187.3500\",\n" + "            \"3. low\": \"186.8200\",\n" + "            \"4. close\": \"186.8999\",\n" + "            \"5. volume\": \"48047\"\n" + "        },\n" + "        \"2021-07-29 14:00:00\": {\n" + "            \"1. open\": \"186.8300\",\n" + "            \"2. high\": \"187.5300\",\n" + "            \"3. low\": \"186.7900\",\n" + "            \"4. close\": \"187.1200\",\n" + "            \"5. volume\": \"43900\"\n" + "        },\n" + "        \"2021-07-29 13:00:00\": {\n" + "            \"1. open\": \"186.4400\",\n" + "            \"2. high\": \"186.9100\",\n" + "            \"3. low\": \"186.3400\",\n" + "            \"4. close\": \"186.8100\",\n" + "            \"5. volume\": \"60328\"\n" + "        },\n" + "        \"2021-07-29 12:00:00\": {\n" + "            \"1. open\": \"185.7100\",\n" + "            \"2. high\": \"186.7150\",\n" + "            \"3. low\": \"185.7100\",\n" + "            \"4. close\": \"186.4300\",\n" + "            \"5. volume\": \"72065\"\n" + "        },\n" + "        \"2021-07-29 11:00:00\": {\n" + "            \"1. open\": \"185.4950\",\n" + "            \"2. high\": \"185.9400\",\n" + "            \"3. low\": \"184.5500\",\n" + "            \"4. close\": \"185.6700\",\n" + "            \"5. volume\": \"98601\"\n" + "        },\n" + "        \"2021-07-29 10:00:00\": {\n" + "            \"1. open\": \"185.8900\",\n" + "            \"2. high\": \"186.4600\",\n" + "            \"3. low\": \"184.8400\",\n" + "            \"4. close\": \"185.4900\",\n" + "            \"5. volume\": \"78011\"\n" + "        },\n" + "        \"2021-07-28 17:00:00\": {\n" + "            \"1. open\": \"185.5300\",\n" + "            \"2. high\": \"185.6600\",\n" + "            \"3. low\": \"185.5300\",\n" + "            \"4. close\": \"185.6600\",\n" + "            \"5. volume\": \"7017\"\n" + "        },\n" + "        \"2021-07-28 16:00:00\": {\n" + "            \"1. open\": \"185.8800\",\n" + "            \"2. high\": \"185.9200\",\n" + "            \"3. low\": \"185.0700\",\n" + "            \"4. close\": \"185.4800\",\n" + "            \"5. volume\": \"138297\"\n" + "        },\n" + "        \"2021-07-28 15:00:00\": {\n" + "            \"1. open\": \"184.6300\",\n" + "            \"2. high\": \"186.0600\",\n" + "            \"3. low\": \"184.4600\",\n" + "            \"4. close\": \"185.9700\",\n" + "            \"5. volume\": \"70013\"\n" + "        },\n" + "        \"2021-07-28 14:00:00\": {\n" + "            \"1. open\": \"184.5900\",\n" + "            \"2. high\": \"184.9300\",\n" + "            \"3. low\": \"184.4200\",\n" + "            \"4. close\": \"184.6100\",\n" + "            \"5. volume\": \"48135\"\n" + "        },\n" + "        \"2021-07-28 13:00:00\": {\n" + "            \"1. open\": \"184.7900\",\n" + "            \"2. high\": \"184.9299\",\n" + "            \"3. low\": \"184.1500\",\n" + "            \"4. close\": \"184.6100\",\n" + "            \"5. volume\": \"73961\"\n" + "        },\n" + "        \"2021-07-28 12:00:00\": {\n" + "            \"1. open\": \"185.2800\",\n" + "            \"2. high\": \"185.4300\",\n" + "            \"3. low\": \"184.4300\",\n" + "            \"4. close\": \"184.7400\",\n" + "            \"5. volume\": \"64260\"\n" + "        },\n" + "        \"2021-07-28 11:00:00\": {\n" + "            \"1. open\": \"185.2000\",\n" + "            \"2. high\": \"185.7900\",\n" + "            \"3. low\": \"184.8900\",\n" + "            \"4. close\": \"185.3600\",\n" + "            \"5. volume\": \"66831\"\n" + "        },\n" + "        \"2021-07-28 10:00:00\": {\n" + "            \"1. open\": \"185.7700\",\n" + "            \"2. high\": \"185.9700\",\n" + "            \"3. low\": \"184.9000\",\n" + "            \"4. close\": \"185.3800\",\n" + "            \"5. volume\": \"47898\"\n" + "        },\n" + "        \"2021-07-27 17:00:00\": {\n" + "            \"1. open\": \"185.6900\",\n" + "            \"2. high\": \"185.6900\",\n" + "            \"3. low\": \"185.6900\",\n" + "            \"4. close\": \"185.6900\",\n" + "            \"5. volume\": \"6879\"\n" + "        },\n" + "        \"2021-07-27 16:00:00\": {\n" + "            \"1. open\": \"186.0100\",\n" + "            \"2. high\": \"186.0100\",\n" + "            \"3. low\": \"185.2700\",\n" + "            \"4. close\": \"185.7000\",\n" + "            \"5. volume\": \"198700\"\n" + "        },\n" + "        \"2021-07-27 15:00:00\": {\n" + "            \"1. open\": \"185.4800\",\n" + "            \"2. high\": \"186.1800\",\n" + "            \"3. low\": \"185.4800\",\n" + "            \"4. close\": \"185.8650\",\n" + "            \"5. volume\": \"48885\"\n" + "        },\n" + "        \"2021-07-27 14:00:00\": {\n" + "            \"1. open\": \"185.8550\",\n" + "            \"2. high\": \"186.1050\",\n" + "            \"3. low\": \"185.5100\",\n" + "            \"4. close\": \"185.5100\",\n" + "            \"5. volume\": \"48839\"\n" + "        },\n" + "        \"2021-07-27 13:00:00\": {\n" + "            \"1. open\": \"186.3600\",\n" + "            \"2. high\": \"186.4100\",\n" + "            \"3. low\": \"185.5950\",\n" + "            \"4. close\": \"185.8600\",\n" + "            \"5. volume\": \"58364\"\n" + "        },\n" + "        \"2021-07-27 12:00:00\": {\n" + "            \"1. open\": \"186.9600\",\n" + "            \"2. high\": \"187.1799\",\n" + "            \"3. low\": \"185.8050\",\n" + "            \"4. close\": \"186.3400\",\n" + "            \"5. volume\": \"60193\"\n" + "        },\n" + "        \"2021-07-27 11:00:00\": {\n" + "            \"1. open\": \"187.6400\",\n" + "            \"2. high\": \"188.0800\",\n" + "            \"3. low\": \"186.5200\",\n" + "            \"4. close\": \"187.1800\",\n" + "            \"5. volume\": \"81460\"\n" + "        },\n" + "        \"2021-07-27 10:00:00\": {\n" + "            \"1. open\": \"185.9300\",\n" + "            \"2. high\": \"187.9900\",\n" + "            \"3. low\": \"185.2400\",\n" + "            \"4. close\": \"187.8650\",\n" + "            \"5. volume\": \"55073\"\n" + "        },\n" + "        \"2021-07-26 17:00:00\": {\n" + "            \"1. open\": \"186.5800\",\n" + "            \"2. high\": \"186.7500\",\n" + "            \"3. low\": \"186.5800\",\n" + "            \"4. close\": \"186.7500\",\n" + "            \"5. volume\": \"3936\"\n" + "        },\n" + "        \"2021-07-26 16:00:00\": {\n" + "            \"1. open\": \"186.5800\",\n" + "            \"2. high\": \"186.6800\",\n" + "            \"3. low\": \"186.0404\",\n" + "            \"4. close\": \"186.5700\",\n" + "            \"5. volume\": \"171595\"\n" + "        },\n" + "        \"2021-07-26 15:00:00\": {\n" + "            \"1. open\": \"186.8200\",\n" + "            \"2. high\": \"186.9200\",\n" + "            \"3. low\": \"186.4600\",\n" + "            \"4. close\": \"186.5800\",\n" + "            \"5. volume\": \"61484\"\n" + "        },\n" + "        \"2021-07-26 14:00:00\": {\n" + "            \"1. open\": \"186.6000\",\n" + "            \"2. high\": \"186.9500\",\n" + "            \"3. low\": \"186.4100\",\n" + "            \"4. close\": \"186.7800\",\n" + "            \"5. volume\": \"56070\"\n" + "        },\n" + "        \"2021-07-26 13:00:00\": {\n" + "            \"1. open\": \"186.8700\",\n" + "            \"2. high\": \"187.0100\",\n" + "            \"3. low\": \"186.4400\",\n" + "            \"4. close\": \"186.5400\",\n" + "            \"5. volume\": \"58590\"\n" + "        },\n" + "        \"2021-07-26 12:00:00\": {\n" + "            \"1. open\": \"185.8700\",\n" + "            \"2. high\": \"186.7700\",\n" + "            \"3. low\": \"185.7800\",\n" + "            \"4. close\": \"186.7599\",\n" + "            \"5. volume\": \"72961\"\n" + "        },\n" + "        \"2021-07-26 11:00:00\": {\n" + "            \"1. open\": \"185.5400\",\n" + "            \"2. high\": \"185.9100\",\n" + "            \"3. low\": \"184.4500\",\n" + "            \"4. close\": \"185.8700\",\n" + "            \"5. volume\": \"107719\"\n" + "        },\n" + "        \"2021-07-26 10:00:00\": {\n" + "            \"1. open\": \"187.1100\",\n" + "            \"2. high\": \"187.5500\",\n" + "            \"3. low\": \"185.5000\",\n" + "            \"4. close\": \"185.5500\",\n" + "            \"5. volume\": \"67469\"\n" + "        },\n" + "        \"2021-07-23 19:00:00\": {\n" + "            \"1. open\": \"187.7600\",\n" + "            \"2. high\": \"187.7600\",\n" + "            \"3. low\": \"187.7600\",\n" + "            \"4. close\": \"187.7600\",\n" + "            \"5. volume\": \"100\"\n" + "        },\n" + "        \"2021-07-23 17:00:00\": {\n" + "            \"1. open\": \"187.7600\",\n" + "            \"2. high\": \"187.7600\",\n" + "            \"3. low\": \"187.7600\",\n" + "            \"4. close\": \"187.7600\",\n" + "            \"5. volume\": \"6544\"\n" + "        },\n" + "        \"2021-07-23 16:00:00\": {\n" + "            \"1. open\": \"187.3950\",\n" + "            \"2. high\": \"187.9100\",\n" + "            \"3. low\": \"186.9500\",\n" + "            \"4. close\": \"187.7900\",\n" + "            \"5. volume\": \"160446\"\n" + "        },\n" + "        \"2021-07-23 15:00:00\": {\n" + "            \"1. open\": \"187.6800\",\n" + "            \"2. high\": \"187.7400\",\n" + "            \"3. low\": \"187.2550\",\n" + "            \"4. close\": \"187.4000\",\n" + "            \"5. volume\": \"70429\"\n" + "        },\n" + "        \"2021-07-23 14:00:00\": {\n" + "            \"1. open\": \"188.3200\",\n" + "            \"2. high\": \"188.4800\",\n" + "            \"3. low\": \"187.6400\",\n" + "            \"4. close\": \"187.6600\",\n" + "            \"5. volume\": \"67461\"\n" + "        },\n" + "        \"2021-07-23 13:00:00\": {\n" + "            \"1. open\": \"187.5600\",\n" + "            \"2. high\": \"188.3900\",\n" + "            \"3. low\": \"187.3200\",\n" + "            \"4. close\": \"188.3900\",\n" + "            \"5. volume\": \"74325\"\n" + "        }\n" + "    }\n" + "}";
}
