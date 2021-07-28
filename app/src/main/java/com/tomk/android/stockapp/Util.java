package com.tomk.android.stockapp;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.ViewGroup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Tom Kowszun on 5/11/2016.
 */

public class Util {

    public static Date stringToDate(String dateString)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date convertedDate = new Date();
        try {
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e) {

            e.printStackTrace();
        }

        return convertedDate;
    }
    public static String dateToString(Date date)
    {
        String convertedDate = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

        convertedDate = dateFormat.format(date);
        return convertedDate;
    }
    // Check to make sure we have Wi Fi
    public static boolean isWiFiAvailable(ConnectivityManager conMan) {
        NetworkInfo networkInfo = conMan.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    // this method will change first letters of each word to caps
    public static String titleCaseConversion(String givenString) {
        if (givenString != null && givenString.length() > 0) {
            String[] arr = givenString.split(" ");
            StringBuilder sb = new StringBuilder();

            for (String anArr : arr) {
                sb.append(Character.toUpperCase(anArr.charAt(0)))
                        .append(anArr.substring(1)).append(" ");
            }
            return sb.toString().trim();
        }
        return null;
    }


    // This is for debugging layouts
    public static void outlineViews(View parent) {
        if (!(parent instanceof ViewGroup)) {
            return;
        }
        ViewGroup vg = (ViewGroup) parent;
        for (int i = 0; i < vg.getChildCount(); i++) {
            View view = vg.getChildAt(i);
            GradientDrawable gd = new GradientDrawable();
            gd.setStroke(1, Color.RED);
            view.setBackgroundDrawable(gd);
            outlineViews(view);
        }
    }

}