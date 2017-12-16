package com.texnoprom.mdam.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.texnoprom.mdam.utils.CustomAuthenticator;


public class AuthenticatorService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        CustomAuthenticator authenticator = new CustomAuthenticator(this);
        return authenticator.getIBinder();
    }
}