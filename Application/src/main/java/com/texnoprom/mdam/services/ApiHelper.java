package com.texnoprom.mdam.services;

import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ParameterType;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiHelper {

    private final static String BASE_URL = "http://95.220.130.189:8080";

    private Retrofit retrofit;
    private ApiHelperInterface apiInterface;

    public ApiHelper() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiInterface = retrofit.create(ApiHelperInterface.class);
    }

    public void getParameterTypes(Callback<List<ParameterType>> callback) {
        Call<List<ParameterType>> call = apiInterface.getParameterTypes();
        call.enqueue(callback);
    }

    public void getCustomData(Callback<List<CustomData>> callback) {
        Call<List<CustomData>> call = apiInterface.getCustomData();
        call.enqueue(callback);
    }

}
