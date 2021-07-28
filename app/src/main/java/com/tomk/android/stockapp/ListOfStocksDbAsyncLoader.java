package com.tomk.android.stockapp;

/**
 * Created by Tom Kowszun
 *
 */

import android.content.Context;

import java.util.ArrayList;

/**
 * This Loader loads a list of stocks from the DB that is later displayed
 * in recycler view.
 */
public class ListOfStocksDbAsyncLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<StockListItem>> {


    private final StockDbAdapter stockDbAdapter;


    public ListOfStocksDbAsyncLoader(Context context, StockDbAdapter adapter) {
        super(context);

        this.stockDbAdapter = adapter;

    }

    @Override
    public ArrayList<StockListItem> loadInBackground() {
        return stockDbAdapter.getStocksList();
    }
}
