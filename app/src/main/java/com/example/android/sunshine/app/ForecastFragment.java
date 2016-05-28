package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * @author Irving
 * @since 25/01/2016
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String LOG_TAG = "LOG_ForecastFragment";
    public static String EXTRA_DETAIL = "extraDetail";

    private static final String[] FORECAST_COLUMNS = {
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

    public static int LOADER_ID = 111;

    private ForecastAdapter forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);

        forecastAdapter = new ForecastAdapter(getActivity(), null, 0);
        listView.setAdapter(forecastAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    String locationSetting = Utility.getPreferredLocation(getActivity());
                    Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(
                        locationSetting, cursor.getLong(COL_WEATHER_DATE));
                    Intent intent = new Intent(getActivity(), DetailActivity.class).setData(uri);
                    startActivity(intent);
                }
            }
        });
        // Checking connection
//        ConnectivityManager connManager =
//                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
//        if (networkInfo == null || !networkInfo.isConnected()) {
//            Toast.makeText(getActivity(), "No internet Connection", Toast.LENGTH_SHORT).show();
//        }


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeather();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        if (idItem == R.id.action_refresh) {
            updateWeather();
            return true;
        } else if (idItem == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        } else if (idItem == R.id.action_show_map) {
            openMapFromPreferences();
        }
        return super.onOptionsItemSelected(item);
    }

    private void openMapFromPreferences()
    {
        String postalCode = Utility.getPreferredLocation(getActivity());

        Uri geoPoint = Uri.parse("geo:0,0?").buildUpon()
            .appendQueryParameter("q", postalCode).build();

        Intent intent = new Intent(Intent.ACTION_VIEW, geoPoint);
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + postalCode + ", no receiving apps installed!");
        }
    }

    private void updateWeather() {
        String location = Utility.getPreferredLocation(getActivity());
        new FetchWeatherTask(getActivity()).execute(location);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String locationSetting = Utility.getPreferredLocation(getActivity());

        // Sort order: Ascending by date
        String sortOrder = WeatherContract.WeatherEntry.COLUMN_DATE + " ASC";
        Uri weatherForLocationUri = WeatherContract.WeatherEntry.buildWeatherLocationWithStartDate(
         locationSetting, System.currentTimeMillis());

        return new CursorLoader(getActivity(), weatherForLocationUri, FORECAST_COLUMNS, null, null,
                                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        forecastAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        forecastAdapter.swapCursor(null);
    }
}

