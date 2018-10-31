package com.texnoprom.mdam.activities;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.fragments.CalibrationsFragment;
import com.texnoprom.mdam.fragments.ConfigFragment;
import com.texnoprom.mdam.fragments.MiscFragment;
import com.texnoprom.mdam.fragments.ValuesFragment;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.models.RegisterInfo;
import com.texnoprom.mdam.services.ApiHelper;
import com.texnoprom.mdam.utils.Modbus;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import retrofit2.Callback;

public class BTActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    BluetoothSPP bt;
    TextView device, status, connectedTo;
    DrawerLayout drawer;
    LinearLayout header;
    Menu menuNav;
    String deviceType, userName, currentFragment;

    ApiHelper apiHelper = new ApiHelper();
    boolean gonnaBeSaved = false;

    public void sen(byte[] data) {
        bt.send(data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        deviceType = getIntent().getStringExtra("deviceType");
        setTitle(deviceType);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        userName = sharedPref.getString("userName", "Not Available");

        ActivityCompat.requestPermissions(BTActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        setContentView(com.texnoprom.mdam.R.layout.activity_bluetooth);
        RegisterInfo.setContext(getApplicationContext());
        setupUI();
        setupBT();
    }

    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) {
                bt.setupService();
                bt.startService();
            }
        }
    }

    public void onPause() {
        super.onPause();
    }

    public void onResume() {
        super.onResume();
        bt.startService();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_measure:
                byte[] MEASURE;
                MEASURE = new byte[]{1, 5, 0, 5, -1, 0, (byte) 156, (byte) 59};

                bt.send(Modbus.MEASURE);
                break;
            default:
                displaySelectedScreen(item.getItemId());
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_bluetooth, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.addUsers:
                bt.stopService();
                Intent changeDevice = new Intent(BTActivity.this, DevicesActivity.class);
                BTActivity.this.startActivity(changeDevice);
                finish();
                return true;
            case R.id.delogin:
                bt.stopService();
                Intent exit = new Intent(BTActivity.this, LoginActivity.class);
                BTActivity.this.startActivity(exit);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void displaySelectedScreen(int itemId) {
        Fragment fragment = null;
        String tag = "";
        switch (itemId) {
            case R.id.nav_read_values:
                fragment = new ValuesFragment();
                tag = "values";
                break;
            case R.id.nav_read_calibration:
                fragment = new CalibrationsFragment();
                tag = "calibration";
                break;
            case R.id.nav_read_config:
                fragment = new ConfigFragment();
                tag = "config";
                break;
            case R.id.nav_read_misc:
                fragment = new MiscFragment();
                tag = "misc";
                break;
        }
        if (fragment != null) {
            currentFragment = tag;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment, tag);
            ft.commit();
        }
    }

    private void setupUI() {
        //Тулбар
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Боковое меню
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //Шапка бокового меню
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        menuNav = navigationView.getMenu();
        navigationView.setNavigationItemSelectedListener(this);
        View headerView = navigationView.getHeaderView(0);
        device = (TextView) headerView.findViewById(R.id.deviceName);
        connectedTo = (TextView) findViewById(R.id.connectedTo);
        status = (TextView) headerView.findViewById(R.id.connectionStatus);
        header = (LinearLayout) headerView.findViewById(R.id.drawerHeader);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.save);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gonnaBeSaved = true;
                bt.send(Modbus.ALL);
            }
        });
    }

    private void setupBT() {
        bt = new BluetoothSPP(this);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bt.setDeviceTarget();
                Context c = getApplicationContext();
                Intent intent = new Intent(c, BTDevicesActivity.class);
                startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                device.setText("Подождите");
                status.setText("Идет подключение");
            }
        });

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(),
                    "Bluetooth is not available", Toast.LENGTH_SHORT).show();
            finish();
        }
        //Состояние подключения в шапке бокового меню
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                connectedTo.setVisibility(View.VISIBLE);
                device.setText("Подключить");
                status.setText("Нет подключения");
                menuNav.setGroupEnabled(R.id.nav_functions, false);
            }

            public void onDeviceConnectionFailed() {
                connectedTo.setVisibility(View.VISIBLE);
                device.setText("Подключить");
                status.setText("Не удалось подключиться");
                menuNav.setGroupEnabled(R.id.nav_functions, false);
            }

            public void onDeviceConnected(String name, String address) {
                connectedTo.setVisibility(View.GONE);
                device.setText(name);
                status.setText("Подключено");
                menuNav.setGroupEnabled(R.id.nav_functions, true);
            }
        });
        //Обработчик принятных данных
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                if (gonnaBeSaved) {
                    RegisterBatch registerBatch = Modbus.RegistersFromData(data, deviceType, 0);
                    apiHelper.postRegisters(registerBatch, postRegistersCallback);
                    gonnaBeSaved = false;
                } else {
                    if (currentFragment == null) {
                        return;
                    }
                    FragmentManager fm = getSupportFragmentManager();
                    switch (currentFragment) {
                        case "values":
                            ValuesFragment vf = (ValuesFragment) fm.findFragmentByTag(currentFragment);
                            vf.handleInputData(data, deviceType);
                            break;
                        case "config":
                            ConfigFragment cf = (ConfigFragment) fm.findFragmentByTag(currentFragment);
                            cf.handleInputData(data, deviceType);
                            break;
                        case "calibration":
                            CalibrationsFragment clf = (CalibrationsFragment) fm.findFragmentByTag(currentFragment);
                            clf.handleInputData(data, deviceType);
                            break;
                        case "misc":
                            MiscFragment mf = (MiscFragment) fm.findFragmentByTag(currentFragment);
                            mf.handleInputData(data, deviceType);
                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (!(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    Toast.makeText(BTActivity.this, "Нет разрешения на доступ к файловой системе. " +
                            "Сохранение будет недоступно.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private Callback<Boolean> postRegistersCallback = new Callback<Boolean>() {
        @Override
        public void onResponse(retrofit2.Call<Boolean> call, retrofit2.Response<Boolean> response) {
            switch (response.code()) {
                case 201:
                    Toast.makeText(BTActivity.this, "Успешно загружено", Toast.LENGTH_SHORT).show();
                    break;
                case 409:
                    Toast.makeText(BTActivity.this, "Регистры уже были загружены", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    Toast.makeText(BTActivity.this, "Ошибка", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onFailure(retrofit2.Call<Boolean> call, Throwable t) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(BTActivity.this, "Нет ответа", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
