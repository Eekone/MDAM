package com.texnoprom.mdam.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.TCTFragmentsAdapter;
import com.texnoprom.mdam.models.RegisterInfo;


public class TCTActivity extends AppCompatActivity {

    private final TCTFragmentsAdapter mTCTFragmentsAdapter = new TCTFragmentsAdapter(getSupportFragmentManager());
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("База данных ТСТ");

        RegisterInfo.setContext(this);
        setContentView(R.layout.activity_tct);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tct_toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.tct_container);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mTCTFragmentsAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tct_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        addFragments();
    }

    private void addFragments() {
        mTCTFragmentsAdapter.addFragment("customData");
        mTCTFragmentsAdapter.addFragment("parameterTypes");
        mTCTFragmentsAdapter.notifyDataSetChanged();
    }





}
