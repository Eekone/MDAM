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
import com.texnoprom.mdam.utils.Bkm1EnteredCalibrationWrite;
import com.texnoprom.mdam.utils.Modbus;


public class CalibrationsFragment extends Fragment {

    RegisterBatch registerBatch;
    ListView listView;
    int position = 0, top;
    SwipeRefreshLayout srl;
    private int expectedStart = -1;
    private int expectedCount = 0;
    private int pendingWrite = -1;
    private final Bkm1EnteredCalibrationWrite enteredWrite = new Bkm1EnteredCalibrationWrite();

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
                        if (isBkm4()) {
                            readCalibrationBlock();
                        } else {
                            writeEnteredCalibrations();
                        }
                    }
                }
        );
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                if (registerBatch == null || arg2 < 0 || arg2 >= registerBatch.getRegisters().size()) {
                    return;
                }
                final int registerAddress = registerBatch.getRegisters().get(arg2).getNumber();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Введите значение:");
                builder.setMessage(isBkm4()
                        ? "Целое число этого регистра"
                        : "Число останется в строке. Потяните список вниз, чтобы записать в модуль");
                final EditText input = new EditText(getActivity());
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);
                builder.setPositiveButton("Ввод", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String raw = input.getText().toString().trim();
                        if (raw.length() == 0) {
                            Toast.makeText(getActivity(), "Введите число", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        try {
                            int value = Integer.parseInt(raw);
                            if (!isBkm4()) {
                                rememberEnteredValue(registerAddress, value);
                                return;
                            }
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
        readCalibrationBlock();
        getActivity().setTitle("Калибровки");
    }

    public void cancelPendingRead() {
        expectedCount = 0;
        pendingWrite = -1;
        enteredWrite.cancel();
    }

    public void reload() {
        if (getActivity() == null) {
            return;
        }
        readCalibrationBlock();
    }

    public void handleInputData(byte[] data, String deviceType) {
        if (enteredWrite.isBusy()) {
            byte[] next = enteredWrite.accept(data);
            if (enteredWrite.consumed()) {
                if (next != null) {
                    ((BTActivity) getActivity()).sen(next);
                } else if (enteredWrite.finished()) {
                    readCalibrationBlock();
                }
                return;
            }
        }
        if (srl != null) {
            srl.setRefreshing(false);
        }
        if (data == null || data.length < 2 || getActivity() == null) {
            return;
        }
        if ((data[1] & 0xFF) == 6 && pendingWrite >= 0) {
            int[] written = Modbus.writtenRegister(data);
            if (written != null && written[0] == pendingWrite) {
                int address = pendingWrite;
                pendingWrite = -1;
                expectedStart = address;
                expectedCount = 1;
                ((BTActivity) getActivity()).sen(Modbus.readHolding(address, 1));
            }
            return;
        }
        if ((data[1] & 0xFF) != 3 || Modbus.holdingValues(data, expectedCount) == null) {
            return;
        }
        if (expectedCount == 1 && registerBatch != null) {
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
        } else {
            registerBatch = Modbus.RegistersFromData(data, deviceType, expectedStart);
            showRegisters();
        }
        expectedCount = 0;
    }

    private void rememberEnteredValue(int registerAddress, int value) {
        if (registerBatch == null) {
            return;
        }
        for (Register row : registerBatch.getRegisters()) {
            if (row.getNumber() == registerAddress) {
                row.setValue(value);
                showRegisters();
                return;
            }
        }
    }

    private void writeEnteredCalibrations() {
        if (registerBatch == null) {
            if (srl != null) {
                srl.setRefreshing(false);
            }
            return;
        }
        int count = 0;
        for (Register row : registerBatch.getRegisters()) {
            int number = row.getNumber();
            if (number >= Bkm1EnteredCalibrationWrite.FIRST && number <= Bkm1EnteredCalibrationWrite.LAST) {
                count++;
            }
        }
        int[] addresses = new int[count];
        int[] values = new int[count];
        int at = 0;
        for (int number = Bkm1EnteredCalibrationWrite.FIRST; number <= Bkm1EnteredCalibrationWrite.LAST; number++) {
            for (Register row : registerBatch.getRegisters()) {
                if (row.getNumber() == number) {
                    addresses[at] = number;
                    values[at] = Math.round(row.getValue());
                    at++;
                    break;
                }
            }
        }
        pendingWrite = -1;
        expectedCount = 0;
        byte[] first = enteredWrite.begin(addresses, values);
        if (first == null) {
            if (srl != null) {
                srl.setRefreshing(false);
            }
            Toast.makeText(getActivity(), "Нет значений для записи", Toast.LENGTH_SHORT).show();
            return;
        }
        ((BTActivity) getActivity()).sen(first);
    }

    private void readCalibrationBlock() {
        pendingWrite = -1;
        expectedStart = calibrationOrigin();
        expectedCount = calibrationCount();
        ((BTActivity) getActivity()).sen(calibrationCommand());
    }

    private void showRegisters() {
        listView.setAdapter(new RegisterAdapter(registerBatch.getRegisters(), getActivity()));
        listView.setSelectionFromTop(position, top);
    }

    private byte[] calibrationCommand() {
        if (isBkm4()) {
            return Modbus.BKM4_CHANNELS;
        }
        return Modbus.CALIBRATIONS;
    }

    private int calibrationOrigin() {
        return isBkm4() ? 0 : 15;
    }

    private int calibrationCount() {
        return isBkm4() ? 32 : 15;
    }

    private boolean isBkm4() {
        return getActivity() instanceof BTActivity
                && "БКМ4".equals(((BTActivity) getActivity()).getDeviceType());
    }
}
