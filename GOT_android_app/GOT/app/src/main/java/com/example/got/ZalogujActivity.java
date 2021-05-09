package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ZalogujActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zaloguj);

        setTitle("Zaloguj");
    }

    public void zaloguj(View view) {
        EditText editTextMail = findViewById(R.id.et_mail_zaloguj);
        EditText editTextHaslo = findViewById(R.id.et_haslo_zaloguj);

        String mail = editTextMail.getText().toString();
        String haslo = editTextHaslo.getText().toString();

        Map<String, String> map = new HashMap<>();
        map.put("mail", mail);
        map.put("haslo", haslo);

        String url = "http://10.0.2.2:5000/login";
        final JSONObject registerJSON = new JSONObject(map);
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(url, registerJSON,
                response -> {
                    // Display the first 500 characters of the response string.
                    try {
                        String status = response.getString("status");
                        int id = response.getInt("id");

                        if (status.equals("Admin") && id == -100) {
                            savePreferences(mail, haslo);
                            Intent adminIntent = new Intent(this, AdminActivity.class);
                            startActivity(adminIntent);
                        } else if (status.equals("Turysta")) {
                            savePreferences(mail, haslo);
                            Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_SHORT).show();
                        } else if (status.equals("Przodownik")) {
                            savePreferences(mail, haslo);
                            Intent przodownikIntent = new Intent(this, PrzodownikActivity.class);
                            startActivity(przodownikIntent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {

        });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }

    private void savePreferences(String mail, String haslo) {
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("mail", mail);
        editor.putString("haslo", haslo);
        editor.apply();
        //sharedPreferences.getString("task1_title", "Default title")
    }
}