package com.texnoprom.mdam.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.adapters.MMPRFragmentsAdapter;
import com.texnoprom.mdam.fragments.TCPFragment;
import com.texnoprom.mdam.models.Register;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;
import com.texnoprom.mdam.services.JSONHelper;
import com.zgkxzx.modbus4And.requset.ModbusParam;
import com.zgkxzx.modbus4And.requset.ModbusReq;
import com.zgkxzx.modbus4And.requset.OnRequestBack;

import java.util.regex.Pattern;

import okhttp3.MediaType;


public class TCPActivity extends AppCompatActivity {

    private final MMPRFragmentsAdapter mMMPRFragmentsAdapter = new MMPRFragmentsAdapter(getSupportFragmentManager());
    private ViewPager mViewPager;
    private String deviceName = "";
    private int deviceNumber;
    ModbusParam mModbusParam = new ModbusParam();

    public static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (deviceName.equals("")) deviceName = getIntent().getStringExtra("deviceType");
        setTitle(deviceName);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        deviceNumber = prefs.getInt("device1", 1);


        RegisterInfo.setContext(this);
        setContentView(R.layout.activity_tcp);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(mMMPRFragmentsAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);


        addFragments();


        modbusInit();
    }

    private void addFragments() {
        String[] MMPRSectionsTitles = RegisterInfo.sections(deviceName);
        if (MMPRSectionsTitles == null) return;

        switch (deviceName) {
            case "ММПР":
                int[] MMPRSectionsNumbers = getResources().getIntArray(R.array.MMPR_sections);
                for (int i = 0; i < MMPRSectionsTitles.length; i++)
                    mMMPRFragmentsAdapter.addFragment(MMPRSectionsTitles[i], deviceName, 3, 1, MMPRSectionsNumbers[i], MMPRSectionsNumbers[i + 1] - MMPRSectionsNumbers[i]);
                mMMPRFragmentsAdapter.notifyDataSetChanged();
                return;
            case "ОПЕ11":
                mMMPRFragmentsAdapter.addFragment(MMPRSectionsTitles[0], deviceName, 2, 1, 0, 5);
                mMMPRFragmentsAdapter.addFragment(MMPRSectionsTitles[1], deviceName, 3, 1, 0, 4);
                mMMPRFragmentsAdapter.addFragment(MMPRSectionsTitles[2], deviceName, 4, 1, 0, 16);

                mMMPRFragmentsAdapter.notifyDataSetChanged();
                return;
            default:
                return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tcp, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.TCPsettings:
                callAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            android.support.v4.app.FragmentTransaction tr = getSupportFragmentManager().beginTransaction();
            tr.replace(R.id.container, mMMPRFragmentsAdapter.getCurrentFragment());
            tr.commit();
            mMMPRFragmentsAdapter.notifyDataSetChanged();
            modbusInit();
        }
    }

    private void callAlert() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        final SharedPreferences.Editor prefEditor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        final Pattern p = Pattern.compile("(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Введите IP, порт, номер устройства");

        final EditText ip = new EditText(this);
        ip.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        ip.setHint(prefs.getString("ip1", "192.168.1.1"));

        final EditText port = new EditText(this);
        port.setHint(String.valueOf(prefs.getInt("port1", 502)));
        port.setInputType(InputType.TYPE_CLASS_NUMBER);
        final EditText device = new EditText(this);
        device.setHint(String.valueOf(prefs.getInt("device1", 1)));
        device.setInputType(InputType.TYPE_CLASS_NUMBER);


        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(ip);
        ll.addView(port);
        ll.addView(device);
        alertDialog.setView(ll);

        alertDialog.setPositiveButton("ОК", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (!ip.getText().toString().equals(""))
                    prefEditor.putString("ip1", ip.getText().toString());
                if (!port.getText().toString().equals(""))
                    prefEditor.putInt("port1", Integer.valueOf(port.getText().toString()));
                if (!device.getText().toString().equals("")) {
                    int dev = Integer.valueOf(device.getText().toString());
                    prefEditor.putInt("device1", dev);
                    mMMPRFragmentsAdapter.resetDevice(dev);
                }
                prefEditor.commit();
                modbusInit();
            }
        });

        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog alert = alertDialog.create();
        alert.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();

        ip.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if ((ip.getText().toString().equals("") || p.matcher(ip.getText()).matches()) && (port.getText().toString().equals("")
                        || !port.getText().toString().equals("") && Integer.valueOf(port.getText().toString()) < 65535)) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);


                } else {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);
                }

            }
        });


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
                if ((ip.getText().toString().equals("") || p.matcher(ip.getText()).matches()) && port.getText().toString().equals("")
                        || !port.getText().toString().equals("") && Integer.valueOf(port.getText().toString()) < 65535) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(true);


                } else {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE)
                            .setEnabled(false);
                }

            }
        });
    }

    public void sendGSON(View v) {
        RegisterBatch registerBatch = new RegisterBatch(deviceName);

        for (TCPFragment fr : mMMPRFragmentsAdapter.fragmentsList) {
            for (Register reg : fr.registerBatch.getRegisters()) {
                registerBatch.getRegisters().add(reg);
            }
        }

        JSONHelper.sendBatch("http://95.220.137.189/post", registerBatch, this);

    }


    private void modbusInit() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        mModbusParam.setHost(prefs.getString("ip1", "192.168.1.1"));
        int p = prefs.getInt("port1", 502);
        mModbusParam.setPort(p);
        mModbusParam.setEncapsulated(false);
        mModbusParam.setKeepAlive(true);
        mModbusParam.setTimeout(3000);
        mModbusParam.setRetries(2);

        ModbusReq.getInstance().setParam(mModbusParam).init(new OnRequestBack<String>() {
            @Override
            public void onSuccess(String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (TCPFragment fr : mMMPRFragmentsAdapter.fragmentsList) {
                            fr.fetchData();
                        }

                    }
                });
                Log.e("MMPR:", "Подклю!чено" + s);
            }

            @Override
            public void onFailed(String msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TCPActivity.this, "Нет подключения", Toast.LENGTH_LONG).show();
                        for (TCPFragment fr : mMMPRFragmentsAdapter.fragmentsList) {
                            fr.fetchData();
                        }
                    }
                });
            }
        });
    }
}
