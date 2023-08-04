package com.metaone.metaone_sdk_demo.api;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

import com.metaone.metaone_sdk_demo.api.response.SampleSSOLoginResponse;


public interface ApiService {
    @POST("https://ws-test.aag.ventures/sso/login")
    Call<SampleSSOLoginResponse> sampleSsoLogin(@Body RequestBody request);
}