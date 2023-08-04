package com.metaone.metaone_sdk_demo.api.response;

import com.google.gson.annotations.SerializedName;

public class SampleSSOLoginResponse {
    @SerializedName("responseCode")
    private String responseCode;

    @SerializedName("token")
    private String token;

    public String getToken() {
        return token;
    }

    public String getResponseCode() {
        return responseCode;
    }

}
