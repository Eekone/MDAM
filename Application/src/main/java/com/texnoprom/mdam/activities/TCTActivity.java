package com.texnoprom.mdam.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.RecyclerDataAdapter2;
import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ExpandingRow;
import com.texnoprom.mdam.services.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TCTActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private Context mContext;
    private ApiHelper apiHelper = new ApiHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_recycler);
        Toolbar toolbar = (Toolbar) findViewById(R.id.tctToolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mContext = TCTActivity.this;
        getCustomData();
    }

    private void getCustomData() {
        apiHelper.getCustomData(new Callback<List<CustomData>>() {
            @Override
            public void onResponse(Call<List<CustomData>> call, Response<List<CustomData>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(TCTActivity.this, "success", Toast.LENGTH_SHORT).show();
                    setAdapter2(response.body());
                } else {
                    Toast.makeText(TCTActivity.this, "Something went wrong, error code: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<CustomData>> call, Throwable t) {
                Toast.makeText(TCTActivity.this, "Failure: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setAdapter2(List<CustomData> customData) {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        RecyclerDataAdapter2 recyclerDataAdapter = new RecyclerDataAdapter2(convertDataToRows2(customData), this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(recyclerDataAdapter);
        mRecyclerView.setHasFixedSize(true);
    }


    private ArrayList<ExpandingRow> convertDataToRows2(List<CustomData> customData) {
        ArrayList<ExpandingRow> expandingRows = new ArrayList<>();

        ExpandingRow expandingRow;

        for (CustomData data : customData) {
            expandingRow = new ExpandingRow();
            expandingRow.setParentName(data.getSessionBeginDateTime());
            expandingRow.setChildDataItems(data.getChildInfo());
            expandingRows.add(expandingRow);
        }

        return expandingRows;
    }
}
