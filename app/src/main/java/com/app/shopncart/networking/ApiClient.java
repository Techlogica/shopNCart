package com.app.shopncart.networking;





import com.app.shopncart.Constant;
import com.app.shopncart.utils.OkHttpUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static final String API_URL = Constant.API_URL;
    private static Retrofit retrofit = null;




    public static Retrofit getApiClient() {



        OkHttpClient okHttpClient = OkHttpUtils.getUnsafeOkHttpClient();


        if (retrofit == null) {

            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            retrofit = new Retrofit.Builder()
                    .baseUrl(API_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;

    }

}