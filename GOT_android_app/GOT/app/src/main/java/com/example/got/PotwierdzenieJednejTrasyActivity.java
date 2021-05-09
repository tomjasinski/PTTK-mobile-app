package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.got.connections.ServerCaller;
import com.example.got.connections.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PotwierdzenieJednejTrasyActivity extends AppCompatActivity implements VolleyCallback {
    private final String SINGLE_TOUR = "SINGLE TOUR";
    private final String CONFIRM_SINGLE_TOUR = "CONFIRM_SINGLE TOUR";
    private final String REJECT_SINGLE_TOUR = "REJECT_SINGLE TOUR";
    private String data_rozpoczecia;
    private String data_zakonczenia;
    private String nazwa;
    private String[] trasyLista;
    private int id_trasy;
    private int liczbaPunktow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potwierdzenie_jednej_trasy);

        setTitle("Potwierdzenie wniosku");
        Bundle extras = getIntent().getExtras();
        nazwa = extras.getString("nazwa");
        id_trasy = extras.getInt("id_trasy");

        downloadData();
    }


    private void downloadData() {
        Map<String, String> map = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        String mail = sharedPreferences.getString("mail", "default_mail");
        String haslo = sharedPreferences.getString("haslo", "default_haslo");
        map.put("mail", mail);
        map.put("haslo", haslo);
        map.put("trasa_id", String.valueOf(id_trasy));

        ServerCaller.downloadSingleTrasaDoPotwierdzenia(this, this, map, SINGLE_TOUR);
    }

    public void potwierdzTrase(View view) {
        Map<String, String> map = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        String mail = sharedPreferences.getString("mail", "default_mail");
        String haslo = sharedPreferences.getString("haslo", "default_haslo");
        map.put("mail", mail);
        map.put("haslo", haslo);
        map.put("trasa_id", String.valueOf(id_trasy));

        ServerCaller.confirmSingleTrasa(this, this, map, CONFIRM_SINGLE_TOUR);
    }

    public void odrzucTrase(View view) {
        Map<String, String> map = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        String mail = sharedPreferences.getString("mail", "default_mail");
        String haslo = sharedPreferences.getString("haslo", "default_haslo");
        map.put("mail", mail);
        map.put("haslo", haslo);
        map.put("trasa_id", String.valueOf(id_trasy));

        ServerCaller.rejectSingleTrasa(this, this, map, REJECT_SINGLE_TOUR);
    }

    @Override
    public void onResponse(JSONObject jsonObject, String callName) {
        if (SINGLE_TOUR.equals(callName)) {
            try {
                data_rozpoczecia = jsonObject.getString("data_rozpoczecia");
                data_zakonczenia = jsonObject.getString("data_zakonczenia");

                JSONArray resultArray = jsonObject.getJSONArray("result");
                trasyLista = new String[resultArray.length()];
                liczbaPunktow = 0;
                for (int i = 0; i < resultArray.length(); i++) {
                    JSONObject singleInfo = resultArray.getJSONObject(i);

                    String punkt_startowy = singleInfo.getString("punkt_startowy");
                    String punkt_koncowy = singleInfo.getString("punkt_koncowy");
                    int punktacja = singleInfo.getInt("punktacja");
                    trasyLista[i] = punktacja + "pkt: " + punkt_startowy + " -> " + punkt_koncowy;
                    liczbaPunktow += punktacja;
                }

                populateInfo();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (CONFIRM_SINGLE_TOUR.equals(callName)) {
            try {
                String status = jsonObject.getString("status");
                if ("OK".equals(status)) {
                    Toast.makeText(this, "Trasa potwierdzona", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }else if (REJECT_SINGLE_TOUR.equals(callName)) {
            try {
                String status = jsonObject.getString("status");
                if ("OK".equals(status)) {
                    Toast.makeText(this, "Trasa została odrzucona", Toast.LENGTH_LONG).show();
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void populateInfo() {
        TextView textViewInfo = findViewById(R.id.tv_info_trasa);
        String simpleInfo = "Wniosek: " + nazwa;
        simpleInfo = simpleInfo + "\nData rozpoczęcia: " + data_rozpoczecia;
        simpleInfo = simpleInfo + "\nData zakończenia: " + data_zakonczenia;
        simpleInfo = simpleInfo + "\nSuma punktów: " + liczbaPunktow;
        textViewInfo.setText(simpleInfo);

        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, trasyLista);

        ListView listView = findViewById(R.id.lv_trasa_info);
        listView.setAdapter(itemsAdapter);
    }
}