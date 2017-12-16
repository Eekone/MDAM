package com.texnoprom.mdam.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.texnoprom.mdam.R;

import java.util.regex.Pattern;

public class TCPSettingsActivity extends AppCompatActivity {


    EditText ip1, port;
    Button save;
    Boolean ipOk = true, portOk = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpsettings);

        Toolbar t = (Toolbar) findViewById(R.id.TCPSettingsToolbar);
        setSupportActionBar(t);
        //getActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        save = (Button) findViewById(R.id.buttonSave);
        save.setClickable(false);
        save.setAlpha(.5f);

        setEditTextsVisibility();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }


    private void setEditTextsVisibility() {
        //String ip=getIntent().getStringExtra("ip");
        final Pattern p = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
        //final Matcher m = p.matcher(ip);
        ip1 = (EditText) findViewById(R.id.ip1);
        ip1.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        ip1.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (ip1.getText().toString().equals("") || p.matcher(ip1.getText()).matches()) {
                    ipOk = true;
                    if (portOk) {
                        save.setClickable(true);
                        save.setAlpha(1f);
                    }

                } else {
                    save.setClickable(false);
                    save.setAlpha(.5f);
                    ipOk = false;
                }

            }
        });
        port = (EditText) findViewById(R.id.port);
        port.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (port.getText().toString().equals("")
                        || !port.getText().toString().equals("") && Integer.valueOf(port.getText().toString()) < 65535)

                {
                    portOk = true;
                    if (ipOk) {
                        save.setClickable(true);
                        save.setAlpha(1f);
                    }
                } else {
                    save.setClickable(false);
                    save.setAlpha(.5f);
                    portOk = false;
                }

            }
        });
        port.setTransformationMethod(new NumericKeyBoardTransformationMethod());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String s = prefs.getString("ip1", "192.168.1.1");
        int i = prefs.getInt("port1", 502);
        ip1.setHint(s);
        port.setHint(String.valueOf(i));

    }

    public void save(View view) {

        SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        if (!ip1.getText().toString().equals(""))
            prefEditor.putString("ip1", ip1.getText().toString());
        if (!port.getText().toString().equals(""))
            prefEditor.putInt("port1", Integer.valueOf(port.getText().toString()));
        prefEditor.apply();

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private class NumericKeyBoardTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return source;
        }
    }
}
