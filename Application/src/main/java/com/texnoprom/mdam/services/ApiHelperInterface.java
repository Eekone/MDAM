package com.texnoprom.mdam.services;

import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ParameterType;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiHelperInterface {

    @GET("/tct/parametertypesjson")
    Call<List<ParameterType>> getParameterTypes();

    @GET("/tct/tets")
    Call<List<CustomData>> getCustomData();

}
