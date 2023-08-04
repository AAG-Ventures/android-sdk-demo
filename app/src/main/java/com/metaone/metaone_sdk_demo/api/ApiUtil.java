package com.metaone.metaone_sdk_demo.api;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class ApiUtil {
    public static RequestBody createRequestBody(Map<String, String> data) {
        JSONObject jsonObject = new JSONObject();
        try {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                jsonObject.put(entry.getKey(), entry.getValue());
            }
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return RequestBody.create(MediaType.parse("application/json"), jsonObject.toString());
    }
}