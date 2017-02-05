package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final String FORECASTFRAGMENT_TAG = "forecastFragTag";

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLocation = Utility.getPreferredLocation(this);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);
        // update the location in our second pane using the fragment manager
        ForecastFragment ff = (ForecastFragment)getSupportFragmentManager()
                                                 .findFragmentByTag(FORECASTFRAGMENT_TAG);
        if (!mLocation.equals(location)) {
            ff.onLocationChanged();
            mLocation = location;
        }
    }
}
