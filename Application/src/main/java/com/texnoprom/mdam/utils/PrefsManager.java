package com.texnoprom.mdam.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefsManager {

    private final static String OBJECT_KEY = "key";
    private SharedPreferences mPref;

    public PrefsManager(Context context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setSomeObject(String json) {
        mPref.edit().putString(OBJECT_KEY, json).apply();
    }

    public String getSomeObject() {
        return mPref.getString(OBJECT_KEY, "");
    }

}
