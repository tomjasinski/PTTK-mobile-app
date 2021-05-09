package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.got.connections.ServerCaller;
import com.example.got.connections.VolleyCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PotwierdzenieTrasyListaWnioskowActivity extends AppCompatActivity implements VolleyCallback {
    private final String TRASY_DO_POTWIERDZENIA = "TRASY DO POTWIERDZENIA";
    private ListView listViewWnioski;
    private int[] idUzytkownikow;
    private String[] nazwyUzytkownikow;
    private int[] idTras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potwierdzenie_trasy_lista_wnioskow);

        setTitle("Wnioski");
        listViewWnioski = findViewById(R.id.lv_lista_wnioskow_do_potwierdzenia);
        idUzytkownikow = new int[0];
        nazwyUzytkownikow = new String[0];
        idTras = new int[0];

    }


    @Override
    protected void onResume() {
        super.onResume();

        downloadData();
    }

    private void downloadData() {
        Map<String, String> map = new HashMap<>();
        SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

        String mail = sharedPreferences.getString("mail", "default_mail");
        String haslo = sharedPreferences.getString("haslo", "default_haslo");
        map.put("mail", mail);
        map.put("haslo", haslo);

        ServerCaller.downloadTrasyDoPotwierdzenia(this, this, map, TRASY_DO_POTWIERDZENIA);
    }

    @Override
    public void onResponse(JSONObject jsonObject, String callName) {
        if (TRASY_DO_POTWIERDZENIA.equals(callName) && jsonObject != null) {
            try {
                String status = jsonObject.getString("status");
                if ("OK".equals(status)) {

                    JSONArray resultArray = jsonObject.getJSONArray("result");

                    idUzytkownikow = new int[resultArray.length()];
                    nazwyUzytkownikow = new String[resultArray.length()];
                    idTras = new int[resultArray.length()];

                    for (int i = 0; i < resultArray.length(); i++) {
                        JSONObject trasa = resultArray.getJSONObject(i);
                        idUzytkownikow[i] = trasa.getInt("uzytkownik_id");
                        nazwyUzytkownikow[i] = trasa.getString("nazwa");
                        idTras[i] = trasa.getInt("trasa_id");
                    }

                    pupulateListView();
                } else {
                    Toast.makeText(this, status, Toast.LENGTH_SHORT).show();
                    clearListView();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                clearListView();
            }
        } else {
            clearListView();
        }
    }

    private void clearListView() {
        listViewWnioski.setAdapter(null);
        String[] singleItemList = new String[1];
        singleItemList[0] = "Brak wniosków";
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, singleItemList);

        listViewWnioski.setAdapter(itemsAdapter);
    }

    private void pupulateListView() {
        ArrayAdapter<String> itemsAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nazwyUzytkownikow);

        listViewWnioski.setAdapter(itemsAdapter);
        listViewWnioski.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PotwierdzenieJednejTrasyActivity.class);
                intent.putExtra("id_trasy", idTras[i]);
                intent.putExtra("nazwa", nazwyUzytkownikow[i]);
                startActivity(intent);
            }
        });
    }
}