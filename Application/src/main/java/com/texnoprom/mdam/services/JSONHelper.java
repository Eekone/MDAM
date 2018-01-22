package com.texnoprom.mdam.services;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.texnoprom.mdam.models.RegisterBatch;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

abstract public class JSONHelper {
    private static final MediaType MEDIA_TYPE =
            MediaType.parse("application/json");
    private static final OkHttpClient client = new OkHttpClient();


   /* public static void sendRegisters(String URL, List<Register> registerBatch, Context con) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<List<Register>>() {
        }.getType();
        String s = gson.toJson(registerBatch, type);
        sendGSON(URL, s, con);
    }*/

    public static void sendBatch(String URL, RegisterBatch registerBatch, Context con) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type type = new TypeToken<RegisterBatch>() {}.getType();
        String s = gson.toJson(registerBatch, type);
        sendGSON(URL, s, con);
    }


    private static void sendGSON(String URL, String s, final Context con) {
        RequestBody body = RequestBody.create(MEDIA_TYPE, s);

        final Request request = new Request.Builder()
                .url(URL)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String mMessage = e.getMessage();
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(con, "Нет ответа", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (response.code()) {
                            case 201:
                                Toast.makeText(con, "Успешно загружено", Toast.LENGTH_SHORT).show();
                                break;
                            case 409:
                                Toast.makeText(con, "Регистры уже были загружены", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                Toast.makeText(con, "Ошибка", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            }
        });
    }
}
