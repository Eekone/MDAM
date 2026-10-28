package com.texnoprom.mdam;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class MdamApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, final Throwable error) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        String detail = error.getMessage();
                        if (detail == null || detail.length() == 0) {
                            detail = error.getClass().getSimpleName();
                        }
                        Toast.makeText(MdamApp.this, "Сбой: " + detail, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }
}
