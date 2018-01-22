package com.texnoprom.mdam.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.RegisterAdapter;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.Arrays;


public class TCPFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public RegisterBatch registerBatch;
    ListView listView;
    SwipeRefreshLayout srl;
    RegisterAdapter adapter;
    public String name, type;
    private int firstRegister, registerCount, command;
    public int device;

    public static TCPFragment newInstance(String category, String type, int command, int device, int firstRegister, int registerCount) {
        TCPFragment fragment = new TCPFragment();

        final Bundle args = new Bundle(3);
        args.putString("category", category);
        args.putInt("command", command);
        args.putInt("firstRegister", firstRegister);
        args.putInt("registerCount", registerCount);
        args.putInt("device", device);
        args.putString("type", type);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.name = getArguments().getString("category");
        this.firstRegister = getArguments().getInt("firstRegister");
        this.registerCount = getArguments().getInt("registerCount");
        this.command = getArguments().getInt("command");
        this.device = getArguments().getInt("device");
        this.type = getArguments().getString("type");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tcp, container, false);
        listView = (ListView) view.findViewById(R.id.regMList);

        srl = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        srl.setOnRefreshListener(this);
        //fetchData(1);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


    }

    public void fetchData() {
        switch (command) {
            case 3:
                ModbusReq.getInstance().readHoldingRegisters(new OnRequestBack<short[]>() {
                    @Override
                    public void onSuccess(final short[] data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();
                                for (int i = 0; i < data.length; i++) {
                                    Register register = new Register(3, i + firstRegister,
                                            RegisterInfo.Name(type, 3, i + firstRegister), data[i]);
                                    registerBatch.getRegisters().add(register);
                                }
                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                            }
                        });
                        Log.d("YEAH", "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    }

                    @Override
                    public void onFailed(final String msg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();

                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                                Log.e("NO", "readHoldingRegisters onFailed " + msg);
                            }
                        });
                    }
                }, device, firstRegister, registerCount);
                break;
            case 2:
                ModbusReq.getInstance().readDiscreteInput(new OnRequestBack<boolean[]>() {
                    @Override
                    public void onSuccess(final boolean[] data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();
                                for (int i = 0; i < data.length; i++) {
                                    Register register = new Register(2, i + firstRegister,
                                            RegisterInfo.Name(type, 2, i + firstRegister), data[i] ? 1 : 0);
                                    registerBatch.getRegisters().add(register);
                                }
                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                            }
                        });
                        Log.d("YEAH", "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    }

                    @Override
                    public void onFailed(final String msg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();

                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                                Log.e("NO", "readHoldingRegisters onFailed " + msg);
                            }
                        });
                    }
                }, device, firstRegister, registerCount);
                break;
            case 4:
                ModbusReq.getInstance().readInputRegisters(new OnRequestBack<short[]>() {
                    @Override
                    public void onSuccess(final short[] data) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();
                                for (int i = 0; i < data.length; i++) {
                                    Register register = new Register(4, i + firstRegister,
                                            RegisterInfo.Name(type, 4, i + firstRegister), data[i]);
                                    registerBatch.getRegisters().add(register);
                                }
                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                            }
                        });
                        Log.d("YEAH", "readHoldingRegisters onSuccess " + Arrays.toString(data));
                    }

                    @Override
                    public void onFailed(final String msg) {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                srl.setRefreshing(false);
                                registerBatch = new RegisterBatch();

                                adapter = new RegisterAdapter(registerBatch.getRegisters(), getActivity());
                                listView.setAdapter(adapter);
                                Log.e("NO", "readHoldingRegisters onFailed " + msg);
                            }
                        });
                    }
                }, device, firstRegister, registerCount);
                break;
            default:
                break;
        }


    }

    @Override
    public void onRefresh() {
        fetchData();
    }

}

