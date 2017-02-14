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
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor>{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String STRING_URI = "param1";
    public static final int LOADER_ID_DA = 112;

    private ShareActionProvider mShareActionProvider;
    private TextView mTextView;
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
        mTextView = (TextView) view.findViewById(R.id.detail_tv);
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
            String message = mTextView.getText().toString() + " #SunshineApp";
            intent.putExtra(Intent.EXTRA_TEXT, message);
            mShareActionProvider.setShareIntent(intent);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(), Uri.parse(queryUri), Utility.FORECAST_COLUMNS, null,
                                null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            boolean isMetric = Utility.isMetric(getActivity());
            String low = Utility.formatTemperature(data.getDouble(Utility.COL_WEATHER_MIN_TEMP),
                                                   isMetric);
            String high = Utility.formatTemperature(data.getDouble(Utility.COL_WEATHER_MAX_TEMP),
                                                    isMetric);
            String stringBuilder = Utility.formatDate(data.getLong(Utility.COL_WEATHER_DATE)) +
                                    " - " + data.getString(Utility.COL_WEATHER_DESC) +
                                    " - " + high + "/" + low;
            mTextView.setText(stringBuilder);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
