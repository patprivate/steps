package com.patfives.steps.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static final String API_ENDPOINT = "https://mechio.box.com";
    public static final String STEPS_REQUEST = "/shared/static/cqfszus3zxxkf6mnbtlycr2btbysdjgz.json";

    private static StepsApiInterface stepsApiInterface;

    private static void build(){
        Gson gson = new GsonBuilder().create();

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ENDPOINT)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        stepsApiInterface = retrofit.create(StepsApiInterface.class);
    }

    public static StepsApiInterface getStepsApiInterface() {
        if(stepsApiInterface == null){
            build();
        }
        return stepsApiInterface;
    }
}
