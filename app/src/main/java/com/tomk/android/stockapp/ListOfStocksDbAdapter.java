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

import java.util.ArrayList;


public class ListOfStocksDbAdapter {
    public static final String DATABASE_NAME = "LIST_STOCKS_DATABASE.db";

    private static final String STOCKS_LIST_TABLE = "STOCKS_LIST_TABLE";

    private static final int DATABASE_VERSION = 201;
    private final Context mCtx;
    private static final String TAG = ListOfStocksDbAdapter.class.getSimpleName();

    private SQLiteDatabase stocksListDatabase;

    private static final String STOCK_SYMBOL = "stockSymbol";
    private static final String STOCK_NAME = "stockName";
    private static final String STOCK_DESCRIPTION = "stockName";
    private static final String MARKET_NAME = "marketName";
    private static final String MARKET_DESCRIPTION = "marketDescription";


    private static final String CREATE_STOCKS_LIST_TABLE = "create table " + STOCKS_LIST_TABLE + "(" +
            STOCK_SYMBOL + " STRING PRIMARY KEY not null UNIQUE," +
            STOCK_NAME + " TEXT," +
            STOCK_DESCRIPTION + " TEXT," +
            MARKET_NAME + " TEXT," +
            MARKET_DESCRIPTION + " TEXT" + ");";


    public ListOfStocksDbAdapter(Context ctx) {
        this.mCtx = ctx;

    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_STOCKS_LIST_TABLE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + CREATE_STOCKS_LIST_TABLE);
            onCreate(db);
        }
    }

    public void open() throws SQLException {
        DatabaseHelper mDbHelper = new DatabaseHelper(mCtx);
        try {
            stocksListDatabase = mDbHelper.getWritableDatabase();
        } catch (Exception ex) {
            String outS = " ************ Exception opening database " + ex;
            Log.i(" *=* ", outS);
        }

        String dbPath = stocksListDatabase.getPath();

        final ArrayList<String> dirArray = new ArrayList<>();
        Cursor c = stocksListDatabase.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        while (c.moveToNext()) {
            String s = c.getString(0);
            if (!s.equals("android_metadata")) {
                dirArray.add(s);
                String outS = " z================>  Table name " + s + " " + dbPath + " is DB open ? " + stocksListDatabase.isOpen();
                Log.i(" *=* ", outS);
            }
        }
        c.close();
    }


    public long insertStocksList(ArrayList<StockListItem> stocksList, boolean replace) {

        long result = (long) 0.0;
        if (replace) {
            for (StockListItem item : stocksList) {

                try {
                    ContentValues newValues = new ContentValues();

                    newValues.put(ListOfStocksDbAdapter.STOCK_SYMBOL, item.getStockSymbol());
                    newValues.put(ListOfStocksDbAdapter.STOCK_NAME, item.getStockName());
                    newValues.put(ListOfStocksDbAdapter.STOCK_DESCRIPTION, item.getStockDescription());
                    newValues.put(ListOfStocksDbAdapter.MARKET_NAME, item.getMarketName());
                    newValues.put(ListOfStocksDbAdapter.MARKET_DESCRIPTION, item.getMarketDescription());
                    result = stocksListDatabase.insertWithOnConflict(STOCKS_LIST_TABLE, null, newValues, SQLiteDatabase.CONFLICT_IGNORE);
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
        Cursor stockCursor = stocksListDatabase.rawQuery("select * from " + STOCKS_LIST_TABLE, null);
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
            result = stocksListDatabase.delete(STOCKS_LIST_TABLE, null, null) > 0;
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
