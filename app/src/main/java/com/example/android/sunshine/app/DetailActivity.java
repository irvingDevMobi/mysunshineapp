package com.example.android.sunshine.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailActivity extends ActionBarActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        if (intent != null) {
            String query = intent.getDataString();
            DetailFragment fragment = DetailFragment.newInstance(query);
            getSupportFragmentManager().beginTransaction().add(R.id.detail_container,
                                                               fragment).commit();
        }
    }

}
