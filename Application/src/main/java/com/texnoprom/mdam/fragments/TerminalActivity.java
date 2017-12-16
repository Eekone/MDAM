package com.texnoprom.mdam.fragments;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.texnoprom.mdam.R;
import com.texnoprom.mdam.activities.BTDevicesActivity;
import com.texnoprom.mdam.utils.Converter;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.BluetoothConnectionListener;
import app.akexorcist.bluetotohspp.library.BluetoothSPP.OnDataReceivedListener;
import app.akexorcist.bluetotohspp.library.BluetoothState;

public class TerminalActivity extends AppCompatActivity {
    //ToDo: оформление в форме запроос-ответ, подсказки при вводе/выбор функции и параметров
    BluetoothSPP bt;
    TextView textStatus, textRead;
    EditText etMessage;

    Menu menu;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        textRead = (TextView) findViewById(R.id.textRead);
        textStatus = (TextView) findViewById(R.id.textStatus);
        etMessage = (EditText) findViewById(R.id.etMessage);

        bt = new BluetoothSPP(this);

        if (!bt.isBluetoothAvailable()) {
            Toast.makeText(getApplicationContext(), "Bluetooth is not available",
                    Toast.LENGTH_SHORT).show();
            finish();  //Уведомление внизу экрана
        }

        bt.setOnDataReceivedListener(new OnDataReceivedListener() {
            public void onDataReceived(byte[] data, String message) {
                textRead.append(Converter.bytesToHex(data) + "\r\n"); //Вывод полученных данных
            }
        });

        bt.setBluetoothConnectionListener(new BluetoothConnectionListener() {
            public void onDeviceDisconnected() {
                textStatus.setText("Нет подключения");
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_connection, menu);
            }

            public void onDeviceConnectionFailed() {
                textStatus.setText("Не удалось подключиться");
            }

            public void onDeviceConnected(String name, String address) {
                textStatus.setText("Подключено к " + name);
                menu.clear();
                getMenuInflater().inflate(R.menu.menu_disconnection, menu);
            }
        });

        Toolbar t = (Toolbar) findViewById(R.id.termToolbar);
        setSupportActionBar(t);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_connection, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_device_connect) {
            bt.setDeviceTarget();
            Intent intent = new Intent(getApplicationContext(), BTDevicesActivity.class);
            startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
        } else if (id == R.id.menu_disconnect) {
            if (bt.getServiceState() == BluetoothState.STATE_CONNECTED)
                bt.disconnect();
        }
        return super.onOptionsItemSelected(item);
    }

    public void onDestroy() {
        super.onDestroy();
        bt.stopService();
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
                setup();
            }
        }
    }

    public void setup() {
        Button btnSend = (Button) findViewById(R.id.btnSend);
        //ToDo: использовать функцию класса ModBus вместо хардкода
        btnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                bt.send(new byte[]{1, 3, 0, 36, 0, 1, -60, 1});
                etMessage.setText("1");
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK)
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) {
            if (resultCode == Activity.RESULT_OK) {
                bt.setupService();
                bt.startService();
                setup();
            } else {
                Toast.makeText(getApplicationContext(), "Bluetooth was not enabled.",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
