package com.texnoprom.mdam.services;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ParameterType;
import com.texnoprom.mdam.models.RegisterBatch;
import com.texnoprom.mdam.utils.ServerAddress;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {

    private final Context appContext;
    private String baseUrl;
    private ApiHelperInterface apiInterface;

    public ApiHelper(Context context) {
        appContext = context.getApplicationContext();
        client();
    }

    private ApiHelperInterface client() {
        String url = ServerAddress.get(appContext);
        if (apiInterface == null || !url.equals(baseUrl)) {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();
            baseUrl = url;
            apiInterface = new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
                    .create(ApiHelperInterface.class);
        }
        return apiInterface;
    }

    public void getParameterTypes(Callback<List<ParameterType>> callback) {
        Call<List<ParameterType>> call = client().getParameterTypes();
        call.enqueue(callback);
    }

    public void getCustomData(Callback<List<CustomData>> callback) {
        Call<List<CustomData>> call = client().getCustomData();
        call.enqueue(callback);
    }

    public void postRegisters(RegisterBatch registerBatch, Callback<Boolean> callback) {
        client().postRegisters(registerBatch).enqueue(callback);
    }

}
