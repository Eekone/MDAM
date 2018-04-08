package com.texnoprom.mdam.services;

import com.texnoprom.mdam.models.CustomData;
import com.texnoprom.mdam.models.ParameterType;
import com.texnoprom.mdam.models.RegisterBatch;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiHelperInterface {

    @GET("/tct/parametertypesjson")
    Call<List<ParameterType>> getParameterTypes();

    @GET("/tct/customqueryjson")
    Call<List<CustomData>> getCustomData();

    @POST("/sotca/post")
    Call<Boolean> postRegisters(@Body RegisterBatch registerBatch);
}
