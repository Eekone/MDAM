package com.texnoprom.mdam.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.TCTFragmentsAdapter;
import com.texnoprom.mdam.fragments.TCTFragment;
import com.texnoprom.mdam.models.RegisterInfo;
import com.texnoprom.mdam.utils.ServerAddress;


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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tct, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.server_address) {
            showServerAddressDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showServerAddressDialog() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setSingleLine(true);
        input.setText(ServerAddress.get(this));
        input.setSelection(input.getText().length());
        new AlertDialog.Builder(this)
                .setTitle("Адрес сервера")
                .setMessage("Например http://192.168.1.20:8080")
                .setView(input)
                .setPositiveButton("Сохранить", new android.content.DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(android.content.DialogInterface dialog, int which) {
                        if (!ServerAddress.save(TCTActivity.this, input.getText().toString())) {
                            Toast.makeText(TCTActivity.this,
                                    "Введите адрес сервера, например http://192.168.1.20:8080",
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        Toast.makeText(TCTActivity.this,
                                "Сервер: " + ServerAddress.get(TCTActivity.this),
                                Toast.LENGTH_SHORT).show();
                        for (TCTFragment fragment : mTCTFragmentsAdapter.fragmentsList) {
                            fragment.reload();
                        }
                    }
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}
