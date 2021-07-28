package com.tomk.android.stockapp;

/**
 * Created by Tom Kowszun
 *
 */

import android.content.Context;

import com.tomk.android.stockapp.models.StockResponse;

/**
 * This Loader loads the full data from the DB that is later displayed
 * in a graph.
 */
public class StockDbAsyncLoader extends android.support.v4.content.AsyncTaskLoader<StockResponse> {


    private final StockDbAdapter stockDbAdapter;


    public StockDbAsyncLoader(Context context, StockDbAdapter adapter) {
        super(context);

        this.stockDbAdapter = adapter;

    }

    @Override
    public StockResponse loadInBackground() {
        return stockDbAdapter.getStockResponse();
    }
}
