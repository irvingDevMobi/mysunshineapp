package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;

public class DetailActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String detail = getIntent().getStringExtra(ForecastFragment.EXTRA_DETAIL);

        TextView textView = (TextView) findViewById(R.id.detail_tv);
        textView.setText(detail);
    }

}
