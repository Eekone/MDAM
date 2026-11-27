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
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.activities.BTActivity;
import com.texnoprom.mdam.adapters.RegisterAdapter;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.utils.Modbus;


public class ConfigFragment extends Fragment {

    RegisterBatch registerBatch;
    ListView listView;
    SwipeRefreshLayout srl;
    private int pendingWrite = -1;
    private int expectedStart = -1;
    private int expectedCount = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_config, container, false);
        listView = (ListView) view.findViewById(R.id.configList);
        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        srl.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        readConfig();
                    }
                }
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                if (getActivity() == null || registerBatch == null
                        || arg2 < 0 || arg2 >= registerBatch.getRegisters().size()) {
                    return;
                }
                final int registerAddress = registerBatch.getRegisters().get(arg2).getNumber();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Введите значение:");
                builder.setMessage("Целое число этого регистра");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Ввод", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (getActivity() == null) {
                            return;
                        }
                        String raw = input.getText().toString().trim();
                        if (raw.length() == 0) {
                            Toast.makeText(getActivity(), "Введите число", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int value = Integer.parseInt(raw);
                            pendingWrite = registerAddress;
                            expectedCount = 0;
                            ((BTActivity) getActivity()).sen(Modbus.presetRegister(1, registerAddress, value));
                        } catch (NumberFormatException e) {
                            Toast.makeText(getActivity(), "Введите число", Toast.LENGTH_SHORT).show();
                        }
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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        readConfig();
        getActivity().setTitle("Конфигурация");
    }

    public void handleInputData(byte[] data, String deviceType) {
        if (data == null || data.length < 2) {
            if (srl != null) {
                srl.setRefreshing(false);
            }
            return;
        }
        if (data[1] == 6 && pendingWrite >= 0) {
            int[] written = Modbus.writtenRegister(data);
            if (written != null && written[0] == pendingWrite && getActivity() != null) {
                int address = pendingWrite;
                pendingWrite = -1;
                expectedStart = address;
                expectedCount = 1;
                ((BTActivity) getActivity()).sen(Modbus.readHolding(address, 1));
            } else {
                pendingWrite = -1;
                if (srl != null) {
                    srl.setRefreshing(false);
                }
            }
            return;
        }
        if (data[1] == 3) {
            if (srl != null) {
                srl.setRefreshing(false);
            }
            if (expectedCount == 1 && registerBatch != null && Modbus.holdingValues(data, 1) != null) {
                RegisterBatch one = Modbus.RegistersFromData(data, deviceType, expectedStart);
                if (!one.getRegisters().isEmpty()) {
                    Register fresh = one.getRegisters().get(0);
                    for (Register row : registerBatch.getRegisters()) {
                        if (row.getNumber() == fresh.getNumber()) {
                            row.setValue(fresh.getValue());
                            break;
                        }
                    }
                    showRegisters();
                }
                expectedCount = 0;
                return;
            }
            if (expectedCount != 1 && Modbus.holdingValues(data, 5) != null) {
                expectedCount = 0;
                registerBatch = Modbus.RegistersFromData(data, deviceType, 10);
                showRegisters();
            }
        } else {
            pendingWrite = -1;
            if (srl != null) {
                srl.setRefreshing(false);
            }
        }
    }

    private void readConfig() {
        pendingWrite = -1;
        expectedStart = 10;
        expectedCount = 5;
        if (getActivity() != null) {
            ((BTActivity) getActivity()).sen(Modbus.CONFIG);
        }
    }

    private void showRegisters() {
        if (listView == null || registerBatch == null || getActivity() == null) {
            return;
        }
        int index = listView.getFirstVisiblePosition();
        View topChild = listView.getChildAt(0);
        int top = topChild == null ? 0 : topChild.getTop();
        listView.setAdapter(new RegisterAdapter(registerBatch.getRegisters(), getActivity()));
        listView.setSelectionFromTop(index, top);
    }
}
