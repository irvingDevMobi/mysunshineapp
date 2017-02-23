package com.example.android.sunshine.app;


import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.android.sunshine.app.data.WeatherContract;
import com.example.android.sunshine.app.data.WeatherContract.WeatherEntry;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STRING_URI = "param1";
    public static final int LOADER_ID_DA = 112;

    private static final String[] DETAIL_COLUMNS = {
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATE,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            WeatherEntry.COLUMN_HUMIDITY,
            WeatherEntry.COLUMN_PRESSURE,
            WeatherEntry.COLUMN_WIND_SPEED,
            WeatherEntry.COLUMN_DEGREES,
            WeatherEntry.COLUMN_WEATHER_ID,
            // This works because the WeatherProvider returns location data joined with
            // weather data, even though they're stored in two different tables.
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING
    };

    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_WEATHER_HUMIDITY = 5;
    public static final int COL_WEATHER_PRESSURE = 6;
    public static final int COL_WEATHER_WIND_SPEED = 7;
    public static final int COL_WEATHER_DEGREES = 8;
    public static final int COL_WEATHER_CONDITION_ID = 9;

    private ShareActionProvider mShareActionProvider;
    private ImageView icon;
    private TextView forecastTextView;
    private TextView dateTextView;
    private TextView highTemTextView;
    private TextView lowTempTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;
    private String queryUri;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param queryUri string with specific uri to a value.
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(String queryUri) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(STRING_URI, queryUri);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            queryUri = getArguments().getString(STRING_URI);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        icon = (ImageView) view.findViewById(R.id.detail_icon);
        forecastTextView = (TextView) view.findViewById(R.id.detail_forecast_textview);
        dateTextView = (TextView) view.findViewById(R.id.detail_date_textview);
        highTemTextView = (TextView) view.findViewById(R.id.detail_high_textview);
        lowTempTextView = (TextView) view.findViewById(R.id.detail_low_textview);
        humidityTextView = (TextView) view.findViewById(R.id.detail_humidity_textview);
        windTextView = (TextView) view.findViewById(R.id.detail_wind_textview);
        pressureTextView = (TextView) view.findViewById(R.id.detail_pressure_textview);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID_DA, null, this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.deatail, menu);
        MenuItem item = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        shareText();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int idItem = item.getItemId();
        if (idItem == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareText() {
        if (mShareActionProvider != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/*");
            String message = dateTextView.getText().toString() + " #SunshineApp";
            intent.putExtra(Intent.EXTRA_TEXT, message);
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Uri.parse(queryUri), DETAIL_COLUMNS, null,
                                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            boolean isMetric = Utility.isMetric(getActivity());
            String forecast = data.getString(COL_WEATHER_DESC);
            String date = Utility.getFriendlyDayString(getContext(),
                                                       data.getLong(COL_WEATHER_DATE));
            String low = Utility.formatTemperature(getActivity(),
                                                   data.getDouble(COL_WEATHER_MIN_TEMP),
                                                   isMetric);
            String high = Utility.formatTemperature(getActivity(),
                                                    data.getDouble(COL_WEATHER_MAX_TEMP),
                                                    isMetric);
            String humidity = getString(R.string.format_humidity,
                                        data.getFloat(COL_WEATHER_HUMIDITY));
            String wind = Utility.getFormattedWind(getActivity(),
                                                   data.getFloat(COL_WEATHER_WIND_SPEED),
                                                   data.getFloat(COL_WEATHER_DEGREES));
            String pressure = getString(R.string.format_pressure,
                                        data.getFloat(COL_WEATHER_PRESSURE));
            int iconResId = Utility.getArtResourceForWeatherCondition(data.getInt(COL_WEATHER_CONDITION_ID));
            forecastTextView.setText(forecast);
            dateTextView.setText(date);
            highTemTextView.setText(high);
            lowTempTextView.setText(low);
            humidityTextView.setText(humidity);
            windTextView.setText(wind);
            pressureTextView.setText(pressure);
            icon.setImageResource(iconResId);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
