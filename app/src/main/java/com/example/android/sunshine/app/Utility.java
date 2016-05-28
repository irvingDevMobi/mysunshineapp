/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.android.sunshine.app.data.WeatherContract;

import java.text.DateFormat;
import java.util.Date;

public class Utility {

    public static final String[] FORECAST_COLUMNS = {
     // In this case the id needs to be fully qualified with a table name, since
     // the content provider joins the location & weather tables in the background
     // (both have an _id column)
     // On the one hand, that's annoying.  On the other, you can search the weather table
     // using the location set by the user, which is only in the Location table.
     // So the convenience is worth it.
     WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
     WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
     WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
     WeatherContract.WeatherEntry.COLUMN_DATE,
     WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_MAX_TEMP = 1;
    public static final int COL_WEATHER_MIN_TEMP = 2;
    public static final int COL_WEATHER_DATE = 3;
    public static final int COL_WEATHER_DESC = 4;

    public static String getPreferredLocation(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_location_key),
         context.getString(R.string.pref_location_default));
    }

    public static boolean isMetric(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_temp_key),
         context.getString(R.string.pref_temp_metric_key))
                .equals(context.getString(R.string.pref_temp_metric_key));
    }

    static String formatTemperature(double temperature, boolean isMetric) {
        double temp;
        if ( !isMetric ) {
            temp = 9*temperature/5+32;
        } else {
            temp = temperature;
        }
        return String.format("%.0f", temp);
    }

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }
}
