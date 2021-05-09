package com.example.got.connections;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.got.database.entities.GrupaGorska;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerCaller {
    public final static String ERROR_NAME = "ERROR";

    public static void downloadGrupaGorska(Context ctx, VolleyCallback volleyCallback, String callName) {
        String url = "http://10.0.2.2:5000/api/groups";
        RequestQueue queue = Volley.newRequestQueue(ctx);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    String string = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        volleyCallback.onResponse(jsonObject, callName);
                        //JSONArray result = new JSONArray(
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            volleyCallback.onResponse(null, "error");
        }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void downloadPunkty(Context ctx, VolleyCallback volleyCallback, String callName, int groupId) {
        String url = "http://10.0.2.2:5000/api/group/" + groupId;
        RequestQueue queue = Volley.newRequestQueue(ctx);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    // Display the first 500 characters of the response string.
                    String string = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    try {
                        JSONObject jsonObject = new JSONObject(string);
                        volleyCallback.onResponse(jsonObject, callName);
                        //JSONArray result = new JSONArray(
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            volleyCallback.onResponse(null, "error");
        }
        );

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public static void register(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/register";
        final JSONObject registerJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, registerJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static void add_path(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/add_path";
        final JSONObject addPathJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, addPathJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static void downloadTrasyDoPotwierdzenia(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/tours_to_confirm";
        final JSONObject addPathJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, addPathJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static void downloadSingleTrasaDoPotwierdzenia(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/single_tour_to_confirm";
        final JSONObject addPathJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, addPathJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static void confirmSingleTrasa(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/confirm_single_tour";
        final JSONObject addPathJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, addPathJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    public static void rejectSingleTrasa(Context ctx, VolleyCallback volleyCallback, Map<String, String> map, String callName) {
        String url = "http://10.0.2.2:5000/reject_single_tour";
        final JSONObject addPathJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(ctx);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, addPathJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    volleyCallback.onResponse(response, callName);
                }, error -> volleyCallback.onResponse(null, ERROR_NAME));

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
