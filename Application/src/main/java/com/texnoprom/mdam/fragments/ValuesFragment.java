package com.texnoprom.mdam.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.activities.BTActivity;
import com.texnoprom.mdam.activities.Modbus;
import com.texnoprom.mdam.adapters.RegisterAdapter;
import com.texnoprom.mdam.models.RegisterBatch;


public class ValuesFragment extends Fragment {

    RegisterBatch registerBatch;
    ListView listView;
    SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_values, container, false);
        listView = (ListView) view.findViewById(R.id.regList);

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ((BTActivity) getActivity()).sen(Modbus.VALUES);
                    }
                }
        );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BTActivity) getActivity()).sen(Modbus.VALUES);
        getActivity().setTitle("Значения");
    }

    public void handleInputData(byte[] data, String deviceType) {
        if (data[1] == 3) {
            srl.setRefreshing(false);
            registerBatch = Modbus.RegistersFromData(data, deviceType, 0);
            RegisterAdapter adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Ввод измеренных значений недоступен");
                    builder.setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
            });
        } else {
            ((BTActivity) getActivity()).sen(Modbus.VALUES);
        }
    }
}

