package com.tomk.android.stockapp;

/**
 * Created by Tom Kowszun.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.tomk.android.stockapp.models.MetaData;
import com.tomk.android.stockapp.models.StockResponse;
import com.tomk.android.stockapp.models.TimeSeriesItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public class StockDbAdapter {
    public static final String DATABASE_NAME = "STOCK_DATABASE.db";

    private static final String STOCK_METADATA_TABLE = "STOCK_METADATA_TABLE";
    private static final String STOCK_TIME_SERIES_TABLE = "STOCK_TIME_SERIES_TABLE";
    private static final String STOCKS_LIST_TABLE = "STOCKS_LIST_TABLE";
    private static final String COMPANY_OVERVIEW_TABLE = "COMPANY_OVERVIEW_TABLE";

    private static final int DATABASE_VERSION = 201;

    private final Context mCtx;
    private static final String TAG = StockDbAdapter.class.getSimpleName();

    private SQLiteDatabase stockDatabase;

    private static final String STOCK_SYMBOL = "stockSymbol";

    private static final String META_INFORMATION = "information";
    private static final String META_LAST_REFRESHED_DATE = "lastRefreshedDate";
    private static final String META_LAST_REFRESHED_TIME = "lastRefreshedTime";
    private static final String META_INTERVAL = "interval";
    private static final String META_OUTPUT_SIZE = "outputSize";
    private static final String META_TIME_ZONE = "timeZone";

    private static final String TIME_SERIES_DATE = "date";
    private static final String TIME_SERIES_INTERVAL = "interval";
    private static final String TIME_SERIES_OPEN = "open";
    private static final String TIME_SERIES_CLOSE = "close";
    private static final String TIME_SERIES_HIGH = "high";
    private static final String TIME_SERIES_LOW = "low";
    private static final String TIME_SERIES_VOLUME = "volume";

    private static final String STOCK_NAME = "stockName";
    private static final String STOCK_DESCRIPTION = "stockDescription";
    private static final String MARKET_NAME = "marketName";
    private static final String MARKET_DESCRIPTION = "marketDescription";

    private static final String COMPANY_OVERVIEW_SYMBOL = "companyOverviewSymbol";
    private static final String COMPANY_OVERVIEW_KEY = "companyOverviewKey";
    private static final String COMPANY_OVERVIEW_VALUE = "companyOverviewValue";

    private static final String CREATE_TABLE_STOCK_METADATA = "create table " + STOCK_METADATA_TABLE + "(" + STOCK_SYMBOL + " STRING PRIMARY KEY not null UNIQUE," + META_INFORMATION + " TEXT," + META_LAST_REFRESHED_DATE + " TEXT," + META_LAST_REFRESHED_TIME + " TEXT," + META_INTERVAL + " TEXT," + META_OUTPUT_SIZE + " TEXT," + META_TIME_ZONE + " TEXT" + ");";

    private static final String CREATE_TABLE_STOCK_TIME_SERIES = "create table " + STOCK_TIME_SERIES_TABLE + "(" + STOCK_SYMBOL + " TEXT," + TIME_SERIES_DATE + " STRING PRIMARY KEY not null UNIQUE," + TIME_SERIES_INTERVAL + " TEXT," + TIME_SERIES_OPEN + " TEXT," + TIME_SERIES_CLOSE + " TEXT," + TIME_SERIES_HIGH + " TEXT," + TIME_SERIES_LOW + " TEXT," + TIME_SERIES_VOLUME + " TEXT" + ");";

    private static final String CREATE_STOCKS_LIST_TABLE = "create table " + STOCKS_LIST_TABLE + "(" + STOCK_SYMBOL + " STRING PRIMARY KEY not null UNIQUE," + STOCK_NAME + " TEXT," + STOCK_DESCRIPTION + " TEXT," + MARKET_NAME + " TEXT," + MARKET_DESCRIPTION + " TEXT" + ");";

    private static final String CREATE_TABLE_COMPANY_OVERVIEW = "create table " + COMPANY_OVERVIEW_TABLE + "(" +  COMPANY_OVERVIEW_SYMBOL + " TEXT," + COMPANY_OVERVIEW_KEY + " STRING PRIMARY KEY not null UNIQUE," + COMPANY_OVERVIEW_VALUE + " TEXT" + ");";

    public StockDbAdapter(Context ctx) {
        this.mCtx = ctx;

    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_STOCKS_LIST_TABLE);
            db.execSQL(CREATE_TABLE_STOCK_METADATA);
            db.execSQL(CREATE_TABLE_STOCK_TIME_SERIES);
            db.execSQL(CREATE_TABLE_COMPANY_OVERVIEW);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + STOCK_METADATA_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STOCK_TIME_SERIES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + STOCKS_LIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + COMPANY_OVERVIEW_TABLE);
            onCreate(db);
        }
    }

    public void open() throws SQLException {
        DatabaseHelper mDbHelper = new DatabaseHelper(mCtx);
        try {
            stockDatabase = mDbHelper.getWritableDatabase();
        } catch (Exception ex) {
            String outS = " ************ Exception opening database " + ex;
            Log.i(" *=* ", outS);
        }

        String dbPath = stockDatabase.getPath();

        final ArrayList<String> dirArray = new ArrayList<>();
        Cursor c = stockDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while (c.moveToNext()) {
            String s = c.getString(0);
            if (!s.equals("android_metadata")) {
                dirArray.add(s);
                String outS = " ================>  Table name " + s + " " + dbPath + " is DB open ? " + stockDatabase.isOpen();
                Log.i(" *=* ", outS);
            }
        }
        c.close();
    }


    public void close() throws SQLException {
        DatabaseHelper mDbHelper = new DatabaseHelper(mCtx);
        try {
            stockDatabase = mDbHelper.getWritableDatabase();
            stockDatabase.close();
        } catch (Exception ex) {
            String outS = " ************ Exception closing database " + ex;
            Log.i(" *=* ", outS);
        }

    }


    public void insertStockResponse(StockResponse stockResponse) {
        deleteAllStockData();
        long resultInsertMD = insertMetaData(stockResponse.getMetaData(), true);
        long resultInsertTS = insertTimeSeries(stockResponse.getTimeSeriesItems(), stockResponse.getMetaData().getSymbol(), true);

        long resultInsertCO = insertCompanyOverview(stockResponse.getCompanyOverviewMap(), stockResponse.getMetaData().getSymbol(), true);

        System.out.println(" 88888888888888888 symbol from CompOve" + stockResponse.getCompanyOverviewMap().get("symbol") + "   symbol from metaData " + stockResponse.getMetaData().getSymbol());
    }


    private long insertMetaData(MetaData metaData, boolean replace) {

        long result = (long) 0.0;
        if (replace) {

            try {
                ContentValues newValues = new ContentValues();
                String convertedDate = Util.dateToString(metaData.getLastRefreshedDate());
                newValues.put(StockDbAdapter.STOCK_SYMBOL, metaData.getSymbol());
                newValues.put(StockDbAdapter.META_INFORMATION, metaData.getInformation());
                newValues.put(StockDbAdapter.META_LAST_REFRESHED_DATE, convertedDate);
                newValues.put(StockDbAdapter.META_INTERVAL, metaData.getInterval());
                newValues.put(StockDbAdapter.META_OUTPUT_SIZE, metaData.getOutputSize());
                newValues.put(StockDbAdapter.META_TIME_ZONE, metaData.getTimeZone());

                result = stockDatabase.insertWithOnConflict(STOCK_METADATA_TABLE, null, newValues, SQLiteDatabase.CONFLICT_IGNORE);
            } catch (IllegalArgumentException e) {
                System.out.println(" IllegalArgumentException " + e.toString());
            } catch (SQLException e) {
                System.out.println(" SQLException " + e.toString());
            } catch (Exception e) {
                System.out.println(" Exception " + e.toString());
            }

        } else {
            result = (long) 0.0;
        }

        return result;
    }

    private long insertTimeSeries(ArrayList<TimeSeriesItem> timeSeriesItems, String stockSymbol, boolean replace) {

        long result = (long) 0.0;
        if (replace) {
            for (TimeSeriesItem item : timeSeriesItems) {

                try {
                    ContentValues newValues = new ContentValues();
                    String convertedDate = Util.dateToString(item.getDate());
                    newValues.put(StockDbAdapter.STOCK_SYMBOL, stockSymbol);

                    newValues.put(StockDbAdapter.TIME_SERIES_DATE, convertedDate);
                    newValues.put(StockDbAdapter.TIME_SERIES_INTERVAL, item.getInterval());
                    newValues.put(StockDbAdapter.TIME_SERIES_OPEN, item.getOpen());
                    newValues.put(StockDbAdapter.TIME_SERIES_CLOSE, item.getClose());
                    newValues.put(StockDbAdapter.TIME_SERIES_HIGH, item.getHigh());
                    newValues.put(StockDbAdapter.TIME_SERIES_LOW, item.getLow());
                    newValues.put(StockDbAdapter.TIME_SERIES_VOLUME, item.getVolume());

                    result = stockDatabase.insertWithOnConflict(STOCK_TIME_SERIES_TABLE, null, newValues, SQLiteDatabase.CONFLICT_IGNORE);
                } catch (IllegalArgumentException e) {
                    System.out.println(TAG + " IllegalArgumentException " + e.toString());
                } catch (SQLException e) {
                    System.out.println(TAG + " SQLException " + e.toString());
                } catch (Exception e) {
                    System.out.println(TAG + " - Exception " + e.toString());
                }
            }
        } else {
            result = (long) 0.0;
        }

        return result;
    }

    public StockResponse getStockResponse() {

        StockResponse stockResponse = new StockResponse();

        // Get the metadata table
        Cursor metaDataCursor = stockDatabase.rawQuery("select * from " + STOCK_METADATA_TABLE, null);
        MetaData metadata = null;
        if (metaDataCursor != null) {
            if (metaDataCursor.moveToFirst()) {
                do {
                    metadata = getMetaDataFromCursor(metaDataCursor);
                } while (metaDataCursor.moveToNext());
            }
        }
        if (metaDataCursor != null) metaDataCursor.close();

        // Get the time series  table
        Cursor stockCursor = stockDatabase.rawQuery("select * from " + STOCK_TIME_SERIES_TABLE, null);
        ArrayList<TimeSeriesItem> timeSeriesList = new ArrayList<>();

        if (stockCursor != null) {
            if (stockCursor.moveToFirst()) {
                do {
                    TimeSeriesItem timeSeriesItem = getTimeSeriesItemFromCursor(stockCursor, stockResponse);
                    timeSeriesList.add(timeSeriesItem);
                } while (stockCursor.moveToNext());
            }
        }
        if (stockCursor != null) stockCursor.close();

        stockResponse.setMetaData(metadata);
        stockResponse.setTimeSeriesItems(timeSeriesList);


        // Get the company overview table
        Cursor companyOverviewCursor = stockDatabase.rawQuery("select * from " + COMPANY_OVERVIEW_TABLE, null);
        LinkedHashMap companyOverviewMap = new LinkedHashMap();

        if (companyOverviewCursor != null) {
            if (companyOverviewCursor.moveToFirst()) {
                do {
                    getCompanyOverviewItemFromCursor(companyOverviewCursor, companyOverviewMap);
                } while (companyOverviewCursor.moveToNext());
            }
        }
        if (companyOverviewCursor != null) companyOverviewCursor.close();

        stockResponse.setCompanyOverviewMap(companyOverviewMap);

        return stockResponse;
    }

    private static TimeSeriesItem getTimeSeriesItemFromCursor(Cursor cursor, StockResponse stockResponse) {

        TimeSeriesItem timeSeriesItem = new TimeSeriesItem();

        Date date = Util.stringToDate(cursor.getString(cursor.getColumnIndex(TIME_SERIES_DATE)));
        timeSeriesItem.setDate(date);

        timeSeriesItem.setInterval(cursor.getInt(cursor.getColumnIndex(TIME_SERIES_INTERVAL)));
        timeSeriesItem.setOpen(cursor.getDouble(cursor.getColumnIndex(TIME_SERIES_OPEN)));
        timeSeriesItem.setClose(cursor.getDouble(cursor.getColumnIndex(TIME_SERIES_CLOSE)));
        timeSeriesItem.setHigh(cursor.getDouble(cursor.getColumnIndex(TIME_SERIES_HIGH)));
        timeSeriesItem.setLow(cursor.getDouble(cursor.getColumnIndex(TIME_SERIES_LOW)));
        timeSeriesItem.setVolume(cursor.getInt(cursor.getColumnIndex(TIME_SERIES_VOLUME)));

        return (timeSeriesItem);
    }

    private static MetaData getMetaDataFromCursor(Cursor cursor) {
        MetaData metaData = new MetaData();

        metaData.setSymbol(cursor.getString(cursor.getColumnIndex(STOCK_SYMBOL)));
        metaData.setInformation(cursor.getString(cursor.getColumnIndex(META_INFORMATION)));
        Date date = Util.stringToDate(cursor.getString(cursor.getColumnIndex(META_LAST_REFRESHED_DATE)));
        metaData.setLastRefreshedDate(date);
        metaData.setOutputSize(cursor.getString(cursor.getColumnIndex(META_OUTPUT_SIZE)));
        metaData.setTimeZone(cursor.getString(cursor.getColumnIndex(META_TIME_ZONE)));

        return (metaData);
    }

    private static void getCompanyOverviewItemFromCursor(Cursor cursor, LinkedHashMap companyOverviewMap) {

        String stockSymbol = cursor.getString(cursor.getColumnIndex(COMPANY_OVERVIEW_SYMBOL));
        String keyCO = cursor.getString(cursor.getColumnIndex(COMPANY_OVERVIEW_KEY));
        String valueCO = cursor.getString(cursor.getColumnIndex(COMPANY_OVERVIEW_VALUE));

        companyOverviewMap.put(keyCO, valueCO);
    }

// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    private long insertCompanyOverview(LinkedHashMap companyOverviewMap, String stockSymbol, boolean replace) {

        long result = (long) 0.0;

        if (replace) {
            Set<String> keys = companyOverviewMap.keySet();
            for (String key : keys) {

                String content = (String) companyOverviewMap.get(key);
                try {
                    ContentValues newValues = new ContentValues();
                    Set<Map.Entry<String, String>> entries = companyOverviewMap.entrySet();

                    newValues.put(StockDbAdapter.COMPANY_OVERVIEW_SYMBOL, stockSymbol);
                    newValues.put(StockDbAdapter.COMPANY_OVERVIEW_KEY, key);
                    newValues.put(StockDbAdapter.COMPANY_OVERVIEW_VALUE, content);

                    result = stockDatabase.insertWithOnConflict(COMPANY_OVERVIEW_TABLE, null, newValues, SQLiteDatabase.CONFLICT_IGNORE);


                } catch (IllegalArgumentException e) {
                    System.out.println(TAG + " IllegalArgumentException " + e.toString());
                } catch (SQLException e) {
                    System.out.println(TAG + " SQLException " + e.toString());
                } catch (Exception e) {
                    System.out.println(TAG + " - Exception " + e.toString());
                }

            }
        } else {
            result = (long) 0.0;
        }

        return result;
    }

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public long insertStocksList(ArrayList<StockListItem> stocksList, boolean replace) {

        long result = (long) 0.0;
        if (replace) {
            for (StockListItem item : stocksList) {

                try {
                    ContentValues newValues = new ContentValues();

                    newValues.put(StockDbAdapter.STOCK_SYMBOL, item.getStockSymbol());
                    newValues.put(StockDbAdapter.STOCK_NAME, item.getStockName());
                    newValues.put(StockDbAdapter.STOCK_DESCRIPTION, item.getStockDescription());
                    newValues.put(StockDbAdapter.MARKET_NAME, item.getMarketName());
                    newValues.put(StockDbAdapter.MARKET_DESCRIPTION, item.getMarketDescription());
                    result = stockDatabase.insertWithOnConflict(STOCKS_LIST_TABLE, null, newValues, SQLiteDatabase.CONFLICT_IGNORE);
                } catch (IllegalArgumentException e) {
                    System.out.println(" IllegalArgumentException " + e.toString());
                } catch (SQLException e) {
                    System.out.println(" SQLException " + e.toString());
                } catch (Exception e) {
                    System.out.println(" Exception " + e.toString());
                }
            }
        } else {
            result = (long) 0.0;
        }

        return result;
    }

    public ArrayList<StockListItem> getStocksList() {

        ArrayList<StockListItem> stocksList = new ArrayList<>();

        // Get the the table
        Cursor stockCursor = stockDatabase.rawQuery("select * from " + STOCKS_LIST_TABLE, null);
        if (stockCursor != null) {
            if (stockCursor.moveToFirst()) {
                do {
                    StockListItem stockListItem = getStocksListItemFromCursor(stockCursor);
                    stocksList.add(stockListItem);
                } while (stockCursor.moveToNext());
            }
        }
        if (stockCursor != null) stockCursor.close();


        return stocksList;
    }

    private static StockListItem getStocksListItemFromCursor(Cursor cursor) {

        StockListItem stockListItem = new StockListItem();

        stockListItem.setStockSymbol(cursor.getString(cursor.getColumnIndex(STOCK_SYMBOL)));
        stockListItem.setStockName(cursor.getString(cursor.getColumnIndex(STOCK_NAME)));
        stockListItem.setStockDescription(cursor.getString(cursor.getColumnIndex(STOCK_DESCRIPTION)));
        stockListItem.setMarketName(cursor.getString(cursor.getColumnIndex(MARKET_NAME)));
        stockListItem.setMarketDescription(cursor.getString(cursor.getColumnIndex(MARKET_DESCRIPTION)));

        return (stockListItem);
    }

    public boolean deleteAllStockListData() {

        boolean result = false;

        try {
            result = stockDatabase.delete(STOCKS_LIST_TABLE, null, null) > 0;
        } catch (IllegalArgumentException e) {
            System.out.println(" IllegalArgumentException " + e.toString());
        } catch (SQLException e) {
            System.out.println(" SQLException " + e.toString());
        } catch (Exception e) {
            System.out.println(" Exception " + e.toString());
        }

        return result;
    }

    public boolean deleteAllStockData() {

        boolean result = false;

        try {
            result = stockDatabase.delete(STOCK_METADATA_TABLE, null, null) > 0;
            result = stockDatabase.delete(STOCK_TIME_SERIES_TABLE, null, null) > 0;
            result = stockDatabase.delete(STOCKS_LIST_TABLE, null, null) > 0;
            result = stockDatabase.delete(COMPANY_OVERVIEW_TABLE, null, null) > 0;
        } catch (IllegalArgumentException e) {
            System.out.println(" IllegalArgumentException " + e.toString());
        } catch (SQLException e) {
            System.out.println(" SQLException " + e.toString());
        } catch (Exception e) {
            System.out.println(" Exception " + e.toString());
        }

        return result;
    }
}
