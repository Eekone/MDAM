package com.texnoprom.mdam.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.texnoprom.mdam.R;
import com.texnoprom.mdam.models.ParameterType;
import com.texnoprom.mdam.services.JSONHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class TCTActivity extends AppCompatActivity implements JSONHelper.NetworkCallback {

    String PARAMETER_TYPES_ENDPOINT = "http://95.220.142.129:8080/tct/parametertypesjson";
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tct);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tctToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        JSONHelper.getJson(PARAMETER_TYPES_ENDPOINT, TCTActivity.this, this);
    }

    public void onSuccess(String response) {
        try {
            List<ParameterType> myObjects = Arrays.asList(objectMapper.readValue(response, ParameterType[].class));
            Toast.makeText(this, "Есть ответ", Toast.LENGTH_LONG).show();
            //populateRecycleView();
        } catch (IOException e) {
            Toast.makeText(this, "Не удалось распарсить", Toast.LENGTH_LONG).show();
        }
    }

    public void onFailure() {
        Toast.makeText(this, "Нет ответа", Toast.LENGTH_LONG).show();
    }
}
