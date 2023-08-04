package com.metaone.metaone_sdk_demo.api;


import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class ApiClient {
    private static Retrofit retrofit = null;

    public static ApiService getClient() {
        if (retrofit == null) {
            OkHttpClient client = new OkHttpClient.Builder().build();

            String baseUrl = "https://demo.com";

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit.create(ApiService.class);
    }
}