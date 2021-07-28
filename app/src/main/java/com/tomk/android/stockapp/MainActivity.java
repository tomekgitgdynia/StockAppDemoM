package com.tomk.android.stockapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tomk.android.stockapp.WebAccess.GetStockIntentService;

import com.tomk.android.stockapp.models.Repository.DataRepository;
import com.tomk.android.stockapp.models.Repository.RepositoryItem;
import com.tomk.android.stockapp.models.StockResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;

/**
 * Created by Tom Kowszun on 11/10/2017.
 */

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, android.support.v4.app.LoaderManager.LoaderCallbacks {

    public static final int DATA_POINTS_NUM = 50;

    public static final String STOCK_SYMBOL = "stockSymbol";
    public static final String INTERVAL = "interval";
    public static final String OUTPUT_SIZE = "outputSize";
    public static final String SERIES_TYPE = "seriesType";
    public static final String API_KEY_OBTAINED = "apiKeyObtained";

    // 1min, 5min, 15min, 30min, 60min
    public static final String INTERVAL_VAL = "60min";

    // compact, full
    public static final String OUTPUT_SIZE_VAL = "compact";
    // open
    public static final String SERIES_TYPE_VAL = "seriesType";
    // UKY832CIXXPKWVJV
    public static final String API_KEY_OBTAINED_VAL = "apiKeyObtained";

    private StockDbAdapter stockDbAdapter;
    private StockListRVadapter stockListRVadapter;
    private CompanyOverviewRVadapter companyOverviewRVadapter;

    private Button barGraphButton;
    private Button lineGraphButton;
    private Button areaGraphButton;
    private android.support.v7.widget.AppCompatTextView refreshed;

    private static final String TAG = "MyActivity";
    private static final String DEFAULT_STOCK = "IBM";
    public static final String STOCK_NOT_FOUND = "Stock Not Found";
    public static final String ERROR_CONNECTING = "Error Connecting";
    public static final String NO_WIFI = "No WiFi";
    public static final String NO_ERRORS = "No Errors";

    private Display display;

    private GraphChart graphChart;
    public static int graphDisplayType = GraphChart.MINOR_GRAPH;

    private static final int STOCK_LOADER_ID = 1;
    private static final int LIST_LOADER_ID = 2;

    private final StockReceiver stockReceiver = new StockReceiver();
    private StockDbAsyncLoader stockDbAsyncLoader;
    private ListOfStocksDbAsyncLoader listOfStocksDbAsyncLoader;
    private RecyclerView recyclerViewStockList;
    private RecyclerView recyclerViewCompanyOverview;
    public boolean blocking = false;
    private android.support.v7.widget.Toolbar toolBar;
    private ProgressBar progressBar;

    private LinearLayout graphPanel;

    private boolean isHomeNow = true;

    private String currentStockDisplayed = null;

    private LinearLayout companyOverviewLayout = null;
    private TextView companyName = null;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setHomeDisplay(!isHomeNow);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void setHomeDisplay(boolean isHome) {
        if (isHome) {
            recyclerViewStockList.setVisibility(View.VISIBLE);
            companyOverviewLayout.setVisibility(View.INVISIBLE);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_my_home);
            toolBar.setTitle(" Select a stock");
            graphPanel.setVisibility(View.GONE);
            this.isHomeNow = true;
        } else {
            recyclerViewStockList.setVisibility(View.INVISIBLE);
            companyOverviewLayout.setVisibility(View.VISIBLE);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_my_return);
            graphChart.changeGraphType(GraphChart.LINEAR_GRAPH);
            toolBar.setTitle(getResources().getString(R.string.Data_for_stock) + " " + currentStockDisplayed);
            graphPanel.setVisibility(View.VISIBLE);
            this.isHomeNow = false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.deleteDatabase(StockDbAdapter.DATABASE_NAME);

        stockDbAdapter = new StockDbAdapter(this.getApplicationContext());
        stockDbAdapter.open();

        setContentView(R.layout.activity_main);


        companyOverviewLayout = findViewById(R.id.company_overview_layout);

        companyName = findViewById(R.id.company_name);

        progressBar = findViewById(R.id.pbLoading);
        progressBar.setVisibility(View.VISIBLE);

        toolBar = findViewById(R.id.stock_activity_toolbar);
        setSupportActionBar(toolBar);

        // add home icon to toolbar
        if (getSupportActionBar() != null) {

            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_my_home);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        recyclerViewStockList = findViewById(R.id.recycler_view_stock_list);
        RecyclerView.LayoutManager recyclerlayoutManagerStockList = new LinearLayoutManager(MainActivity.this);
        recyclerViewStockList.setLayoutManager(recyclerlayoutManagerStockList);
        recyclerViewStockList.setHasFixedSize(true);


        recyclerViewCompanyOverview = findViewById(R.id.recycler_view_company_overview);
        RecyclerView.LayoutManager recyclerlayoutManagerCompanyOverview = new LinearLayoutManager(MainActivity.this);
        recyclerViewCompanyOverview.setLayoutManager(recyclerlayoutManagerCompanyOverview);
        recyclerViewCompanyOverview.setHasFixedSize(true);

        graphPanel = findViewById(R.id.graph_panel);


        // Get stock name from preferences. If not in Preferences, use the default coded in this class.
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String nameOfTheStockEntered = preferences.getString("stockNameKey", "");
        if (nameOfTheStockEntered == null || nameOfTheStockEntered.length() < 1) {
            nameOfTheStockEntered = DEFAULT_STOCK;
        }

        barGraphButton = findViewById(R.id.bar_graph);
        barGraphButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                graphDisplayType = GraphChart.BAR_GRAPH;
                MainActivity.this.graphChart.changeGraphType(graphDisplayType);
            }
        });


        lineGraphButton = findViewById(R.id.line_graph);
        lineGraphButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                graphDisplayType = GraphChart.LINEAR_GRAPH;
                MainActivity.this.graphChart.changeGraphType(graphDisplayType);
            }
        });

        areaGraphButton = findViewById(R.id.area_graph);
        areaGraphButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphDisplayType = GraphChart.FILL_GRAPH;
                MainActivity.this.graphChart.changeGraphType(graphDisplayType);
            }
        });

        // Height of the graph in percent of the total device height
        int heightInPercent = 20;
        int graphHeight = calculatePercentageHeight(heightInPercent);
        graphChart = MainActivity.this.findViewById(R.id.Graph);

        graphChart.getLayoutParams().height = graphHeight;
        graphChart.init(null, graphDisplayType);
        setHomeDisplay(true);
        refreshed = findViewById(R.id.refreshed);
        onItemSelected(nameOfTheStockEntered);
    }

    private int calculatePercentageHeight(int desiredPercentage) {
        int percentHeightInPixels = 0;
        display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        int height = display.getHeight();

        percentHeightInPixels = (desiredPercentage * height) / 100;
        return percentHeightInPixels;
    }

    // Adapter for the recycler view that displays the stock list items in the main activity window
    class StockListRVadapter extends RecyclerView.Adapter<StockListItemVH> {
        ArrayList<StockListItem> stockList;
        private StockListItemVH viewHolder;

        public StockListRVadapter(ArrayList<StockListItem> list) {

            this.stockList = list;
        }

        @Override
        public StockListItemVH onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_stock, parent, false);
            return new StockListItemVH(view);
        }

        @Override
        public void onBindViewHolder(StockListItemVH holder, int position) {

            viewHolder = holder;
            viewHolder.stockSymbol = this.stockList.get(position).getStockSymbol();
            viewHolder.stockSymbolTV.setText(this.stockList.get(position).getStockSymbol());
            viewHolder.stockNameTV.setText(this.stockList.get(position).getStockName());
        }

        @Override
        public int getItemCount() {
            return this.stockList.size();
        }

        public void setItems(ArrayList<StockListItem> list) {
            this.stockList = list;
        }

        public StockListItemVH getViewHolder() {
            return viewHolder;
        }
    }

    private class StockListItemVH extends RecyclerView.ViewHolder {

        public String stockSymbol;
        public final TextView stockSymbolTV;
        public final TextView stockNameTV;

        public StockListItemVH(View itemView) {
            super(itemView);

            stockSymbolTV = itemView.findViewById(R.id.symbol_textview);
            stockSymbolTV.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MainActivity.this.onItemSelected(stockSymbol);
                }
            });

            stockNameTV = itemView.findViewById(R.id.stock_list_textview);
            stockNameTV.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    MainActivity.this.onItemSelected(stockSymbol);
                }
            });
        }
    }

    // Adapter for the recycler view that displays Company Overview the main activity window
    class CompanyOverviewRVadapter extends RecyclerView.Adapter<CompanyOverviewItemVH> {
        final LinkedHashMap<String, String> companyOverviewMap;
        private CompanyOverviewItemVH viewHolder;

        public CompanyOverviewRVadapter(LinkedHashMap<String, String> companyOverviewMap) {
            this.companyOverviewMap = companyOverviewMap;
        }

        @Override
        public CompanyOverviewItemVH onCreateViewHolder(ViewGroup parent, int viewType) {

            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_company_overview, parent, false);
            return new CompanyOverviewItemVH(view);
        }

        @Override
        public void onBindViewHolder(CompanyOverviewItemVH holder, int position) {

            viewHolder = holder;

            // For now this is hardcoded as the full list is very long and key names are not proper English.  Need to decide which of the items are worth showing
            if (position == 0) {
                viewHolder.itemNameTV.setText("Exchange ");
                viewHolder.itemValueTV.setText(companyOverviewMap.get("exchange"));
            } else if (position == 1) {
                viewHolder.itemNameTV.setText("52 week high ");
                viewHolder.itemValueTV.setText(companyOverviewMap.get("_52WeekHigh"));
            } else if (position == 2) {
                viewHolder.itemNameTV.setText("52 week low ");
                viewHolder.itemValueTV.setText(companyOverviewMap.get("_52WeekHigh"));
            } else if (position == 3) {
                viewHolder.itemNameTV.setText("50 day moving average");
                viewHolder.itemValueTV.setText(companyOverviewMap.get("_50DayMovingAverage"));
            } else {
                viewHolder.itemNameTV.setText("Profit Margin ");
                viewHolder.itemValueTV.setText(companyOverviewMap.get("profitMargin"));
            }

        }

        @Override
        public int getItemCount() {
            // Harcoded for now
            return 5;
        }

        public CompanyOverviewItemVH getViewHolder() {
            return viewHolder;
        }
    }

    private class CompanyOverviewItemVH extends RecyclerView.ViewHolder {

        public final TextView itemNameTV;
        public final TextView itemValueTV;

        public CompanyOverviewItemVH(View itemView) {
            super(itemView);

            itemNameTV = itemView.findViewById(R.id.item_name);
            itemValueTV = itemView.findViewById(R.id.item_value);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        progressBar.setVisibility(View.INVISIBLE);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "Saving data - instance state ");
    }

    @Override
    public android.support.v4.content.AsyncTaskLoader onCreateLoader(int id, Bundle args) {

        if (id == STOCK_LOADER_ID) {
            stockDbAsyncLoader = new StockDbAsyncLoader(this.getApplicationContext(), this.stockDbAdapter);
            return stockDbAsyncLoader;
        } else if (id == LIST_LOADER_ID) {
            listOfStocksDbAsyncLoader = new ListOfStocksDbAsyncLoader(this.getApplicationContext(), this.stockDbAdapter);
            return listOfStocksDbAsyncLoader;
        }

        return null;
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader loader, Object data) {

        StockResponse stocksResponse = null;
        ArrayList<StockListItem> listResponse = null;

        if (loader.getId() == STOCK_LOADER_ID) {
            stocksResponse = (StockResponse) data;
            if (stocksResponse != null && stocksResponse.getMetaData() != null) {

                MainActivity.this.graphChart.newStockReceived(stocksResponse);

                Date date = stocksResponse.getMetaData().getLastRefreshedDate();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MM/dd/yyyy");
                String dateString = simpleDateFormat.format(date);

                Log.d(TAG, "Saving symbol to SharedPreferences");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("stockNameKey", stocksResponse.getMetaData().getSymbol());
                editor.apply();

                toolBar.setTitle(getResources().getString(R.string.Data_for_stock) + " " + stocksResponse.getMetaData().getSymbol());
                currentStockDisplayed = stocksResponse.getMetaData().getSymbol();
                refreshed.setText(getResources().getString(R.string.Refreshed) + "  " + dateString);

                LinkedHashMap compOverMap = stocksResponse.getCompanyOverviewMap();
                companyOverviewRVadapter = new CompanyOverviewRVadapter(compOverMap);
                recyclerViewCompanyOverview.setAdapter(companyOverviewRVadapter);
                companyOverviewRVadapter.notifyDataSetChanged();

                setHomeDisplay(false);


                graphPanel.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
            }
        } else if (loader.getId() == LIST_LOADER_ID) {

            try {
                listResponse = (ArrayList<StockListItem>) data;
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (stockListRVadapter == null) {
                stockListRVadapter = new StockListRVadapter(listResponse);
                recyclerViewStockList.setAdapter(stockListRVadapter);
            } else {
                stockListRVadapter = new StockListRVadapter(listResponse);
                recyclerViewStockList.setAdapter(stockListRVadapter);
                stockListRVadapter.notifyDataSetChanged();
            }
        }

        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader loader) {

    }

    public void onItemSelected(String stockSymbol) {

        DataRepository dataRepo = new DataRepository();
        String stockName = null;
        for (RepositoryItem i : dataRepo.getRepository())
        {
            RepositoryItem item = i;
            if(item.getStockSymbol().equals(stockSymbol))
            {
                stockName = item.getStockName();
                companyName.setText(getResources().getString(R.string.Company_name) +  ": " + stockName);
            }
        }

        if (!blocking) {
            blocking = true;
            progressBar.setVisibility(ProgressBar.VISIBLE);
            toolBar.setTitle(getResources().getString(R.string.Looking_for_stock_data) + stockSymbol + " ...");

            Intent getStockDataIntServ = new Intent(getApplicationContext(), GetStockIntentService.class);
            getStockDataIntServ.putExtra(STOCK_SYMBOL, stockSymbol);
            getStockDataIntServ.putExtra(INTERVAL, INTERVAL_VAL);
            getStockDataIntServ.putExtra(OUTPUT_SIZE, OUTPUT_SIZE_VAL);
            getStockDataIntServ.putExtra(SERIES_TYPE, SERIES_TYPE_VAL);
            getStockDataIntServ.putExtra(API_KEY_OBTAINED, API_KEY_OBTAINED_VAL);

            graphPanel.setVisibility(View.VISIBLE);

            startService(getStockDataIntServ);

        } else {
            Log.d(TAG, " >>>>>>>>>>>>>>>>> BLOCKING !!! <<<<<<<<<<<<<< ");
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        progressBar.setVisibility(View.INVISIBLE);
        registerReceiver(stockReceiver, new IntentFilter(GetStockIntentService.STOCK_LIST_ACTION));
        blocking = false;
    }

    @Override
    public void onPause() {
        super.onPause();
        progressBar.setVisibility(View.INVISIBLE);
        unregisterReceiver(stockReceiver);
    }

    public class StockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(GetStockIntentService.STOCK_LIST_ACTION)) {
                String itemCount = intent.getExtras().getString(GetStockIntentService.NUMBER_OF_ITEMS);
                String resultString = intent.getStringExtra(GetStockIntentService.RESULT_STRING);
                Toast toast = null;
                if (Integer.valueOf(itemCount) > 0 && resultString.equals(MainActivity.NO_ERRORS)) {
                    getLoaderManager().destroyLoader(STOCK_LOADER_ID);
                    stockDbAsyncLoader = (StockDbAsyncLoader) getSupportLoaderManager().initLoader(STOCK_LOADER_ID, null, MainActivity.this);

                    stockDbAsyncLoader.forceLoad();

                    getLoaderManager().destroyLoader(LIST_LOADER_ID);
                    listOfStocksDbAsyncLoader = (ListOfStocksDbAsyncLoader) getSupportLoaderManager().initLoader(LIST_LOADER_ID, null, MainActivity.this);
                    progressBar.setVisibility(View.VISIBLE);
                    listOfStocksDbAsyncLoader.forceLoad();
                    Log.i(TAG, " >>>>>>>>>>>>>>>>> Received intent service data " + itemCount);

                }
            }
            blocking = false;
        }
    }
}
