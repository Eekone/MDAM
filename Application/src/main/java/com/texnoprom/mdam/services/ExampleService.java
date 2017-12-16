package com.texnoprom.mdam.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.widget.Chronometer;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;

public class ExampleService extends Service {
    private BluetoothSPP bt;
    private IBinder mBinder = new MyBinder();
    private Chronometer mChronometer;

    @Override
    public void onCreate() {
        super.onCreate();
        bt = new BluetoothSPP(this);
        bt.setDeviceTarget();
       /* Intent intent = new Intent(getApplicationContext(), BTDevicesActivity.class);
        Intent dialogIntent = new Intent(this, BTDevicesActivity.class);

        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(dialogIntent);*/
        mChronometer = new Chronometer(this);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
    }


    @Override
    public IBinder onBind(Intent intent) {
        bt = new BluetoothSPP(getApplicationContext());

        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mChronometer.stop();
    }

    public void sendCommand() {
        byte[] MEASURE = {1, 5, 0, 5, -1, 0, (byte) 156, 59};
        bt.send(MEASURE);
    }

    public String getTimestamp() {
        long elapsedMillis = SystemClock.elapsedRealtime()
                - mChronometer.getBase();
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) (elapsedMillis - hours * 3600000) / 60000;
        int seconds = (int) (elapsedMillis - hours * 3600000 - minutes * 60000) / 1000;
        int millis = (int) (elapsedMillis - hours * 3600000 - minutes * 60000 - seconds * 1000);
        return hours + ":" + minutes + ":" + seconds + ":" + millis;
    }

    public BluetoothSPP getBT() {
        return bt;
    }

    public class MyBinder extends Binder {
        public ExampleService getService() {
            return ExampleService.this;
        }
    }
}