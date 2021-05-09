package com.example.got.connections;

import org.json.JSONObject;

public interface VolleyCallback {
    public void onResponse(JSONObject jsonObject, String callName);
}
