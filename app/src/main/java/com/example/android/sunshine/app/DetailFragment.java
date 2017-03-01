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
 */
public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String DETAIL_URI = "d.URI";
    public static final int DETAIL_LOADER_ID = 112;

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
    private Uri mUri;
    private ImageView icon;
    private TextView forecastTextView;
    private TextView dateTextView;
    private TextView highTemTextView;
    private TextView lowTempTextView;
    private TextView humidityTextView;
    private TextView windTextView;
    private TextView pressureTextView;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mUri = getArguments().getParcelable(DETAIL_URI);
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
        getLoaderManager().initLoader(DETAIL_LOADER_ID, null, this);
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
        if (mUri == null) {
            return null;
        }
        return new CursorLoader(getActivity(), mUri, DETAIL_COLUMNS, null, null, null);
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

    void onLocationChanged(String newLocation) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            mUri = WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            getLoaderManager().restartLoader(DETAIL_LOADER_ID, null, this);
        }
    }
}
