package com.texnoprom.mdam.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.activities.BTActivity;
import com.texnoprom.mdam.adapters.RegisterAdapter;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.utils.Modbus;


public class CalibrationsFragment extends Fragment {

    RegisterBatch registerBatch;
    ListView listView;
    int position = 0, top;
    SwipeRefreshLayout srl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calibrations, container, false);
        listView = (ListView) view.findViewById(R.id.calibList);
        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        ((BTActivity) getActivity()).sen(Modbus.CALIBRATIONS);
                    }
                }
        );
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((BTActivity) getActivity()).sen(Modbus.CALIBRATIONS);
        getActivity().setTitle("Калибровки");
    }

    public void handleInputData(byte[] data, String deviceType) {
        if (data[1] == 3) {
            srl.setRefreshing(false);
            registerBatch = Modbus.RegistersFromData(data, deviceType, 31);
            RegisterAdapter adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
            listView.setAdapter(adapter);
            listView.setSelectionFromTop(position, top);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Введите значение:");
                    builder.setMessage("От 1 до 2");
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_NUMBER);
                    builder.setView(input);
                    builder.setPositiveButton("Ввод", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((BTActivity) getActivity()).sen(Modbus.presetRegister(1,
                                    registerBatch.getRegisters().get(0).getNumber() + arg2,
                                    Integer.valueOf(input.getText().toString())));
                        }
                    });
                    builder.setNegativeButton("Назад", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                }
            });
        } else {
            position = listView.getFirstVisiblePosition();
            View v = listView.getChildAt(0);
            top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
            ((BTActivity) getActivity()).sen(Modbus.CALIBRATIONS);
        }
    }
}
