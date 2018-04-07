package com.texnoprom.mdam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.RecyclerDataAdapter2;
import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ExpandableRowData;
import com.texnoprom.mdam.models.ExpandingRow;
import com.texnoprom.mdam.models.ParameterType;
import com.texnoprom.mdam.services.ApiHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TCTFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    private RecyclerView mRecyclerView;
    private ApiHelper apiHelper = new ApiHelper();

    SwipeRefreshLayout srl;
    public String queryType;

    public static TCTFragment newInstance(String queryType) {
        TCTFragment fragment = new TCTFragment();

        final Bundle args = new Bundle(1);
        args.putString("queryType", queryType);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.queryType = getArguments().getString("queryType");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tct, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.tct_expandable_rview);
        srl = (SwipeRefreshLayout) view.findViewById(R.id.tctSwipeContainer);
        srl.setOnRefreshListener(this);
        srl.setRefreshing(true);
        getData();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onRefresh() {
        getData();
    }

    private void getData() {
        srl.setRefreshing(true);
        mRecyclerView.setAdapter(null);
        switch (queryType) {
            case "customData":
                apiHelper.getCustomData(customDataCallback);
                break;
            case "parameterTypes":
                apiHelper.getParameterTypes(parameterTypesCallback);
                break;
        }
    }

    private void setAdapter(ArrayList<ExpandingRow> data) {
        RecyclerDataAdapter2 recyclerDataAdapter = new RecyclerDataAdapter2(data, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(recyclerDataAdapter);
        mRecyclerView.setHasFixedSize(true);
    }


    private ArrayList<ExpandingRow> converDataToExpandableRows(List<? extends ExpandableRowData> parameterTypes) {
        ArrayList<ExpandingRow> expandingRows = new ArrayList<>();

        ExpandingRow expandingRow;

        for (ExpandableRowData data : parameterTypes) {
            expandingRow = new ExpandingRow();
            expandingRow.setParentName(data.getParentInfo());
            expandingRow.setChildDataItems(data.getChildInfo());
            expandingRows.add(expandingRow);
        }

        return expandingRows;
    }

    private Callback<List<ParameterType>> parameterTypesCallback = new Callback<List<ParameterType>>() {
        @Override
        public void onResponse(Call<List<ParameterType>> call, Response<List<ParameterType>> response) {
            if (response.isSuccessful()) {
                Toast.makeText(getActivity(), "Успешное подключение", Toast.LENGTH_SHORT).show();
                setAdapter(converDataToExpandableRows(response.body()));
            } else {
                Toast.makeText(getActivity(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
            }
            srl.setRefreshing(false);
        }

        @Override
        public void onFailure(Call<List<ParameterType>> call, Throwable t) {
            Toast.makeText(getActivity(), "Нет подключения: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            srl.setRefreshing(false);
        }
    };

    private Callback<List<CustomData>> customDataCallback = new Callback<List<CustomData>>() {
        @Override
        public void onResponse(Call<List<CustomData>> call, Response<List<CustomData>> response) {
            if (response.isSuccessful()) {
                Toast.makeText(getActivity(), "Успешное подключение", Toast.LENGTH_SHORT).show();
                setAdapter(converDataToExpandableRows(response.body()));
            } else {
                Toast.makeText(getActivity(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
            }
            srl.setRefreshing(false);
        }

        @Override
        public void onFailure(Call<List<CustomData>> call, Throwable t) {
            Toast.makeText(getActivity(), "Нет подключения: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            srl.setRefreshing(false);
        }
    };

}

