package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.got.connections.ServerCaller;
import com.example.got.connections.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DodajNowyOdcinekActivity extends AppCompatActivity implements VolleyCallback {
    private final String ADD_PATH = "ADD_PATH";

    HashMap<String, Integer> grupaGorskaMap;
    HashMap<Integer, ArrayList<String>> punktyByGrupaGorskaIdMap;
    String[] nazwyGrupGorskich;
    String[] nazwyPunktow;
    Spinner grupaGorskaSpinner;
    Spinner punktStartowySpinner;
    Spinner punktKoncowySpinner;
    EditText punktacjaEditText;
    EditText punktacjaOdwrotnieEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dodaj_nowy_odcinek);

        setTitle("Dodaj nowy odcinek");

        Bundle extras = getIntent().getExtras();
        grupaGorskaMap = (HashMap<String, Integer>) extras.get("grupy");
        punktyByGrupaGorskaIdMap = (HashMap<Integer, ArrayList<String>>) extras.get("punkty");

        grupaGorskaSpinner = findViewById(R.id.spinner_grupa_gorska_dodaj);
        punktStartowySpinner = findViewById(R.id.spinner_punkt_startowy_dodaj);
        punktKoncowySpinner = findViewById(R.id.spinner_punkt_koncowy_dodaj);
        punktacjaEditText = findViewById(R.id.et_punktacja_dodaj);
        punktacjaOdwrotnieEditText = findViewById(R.id.et_punktacja_odwrotnie_dodaj);

        setGrupaGorskaSpinner();
        setPunktSpinners();
    }

    private void setGrupaGorskaSpinner() {
        nazwyGrupGorskich = grupaGorskaMap.keySet().toArray(new String[0]);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nazwyGrupGorskich);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        grupaGorskaSpinner.setAdapter(adapter);
    }

    private void setPunktSpinners() {
        int currentGrupaGorskaPosition = grupaGorskaSpinner.getSelectedItemPosition();
        String grupaGorskaNazwa = nazwyGrupGorskich[currentGrupaGorskaPosition];
        int grupaGorskaId = grupaGorskaMap.get(grupaGorskaNazwa);

        List<String> punktyList = punktyByGrupaGorskaIdMap.get(grupaGorskaId);
        nazwyPunktow = punktyList.toArray(new String[0]);

        ArrayAdapter<String> adapterStartowy = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nazwyPunktow);
        adapterStartowy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        punktStartowySpinner.setAdapter(adapterStartowy);

        ArrayAdapter<String> adapterKoncowy = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, nazwyPunktow);
        adapterKoncowy.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        punktKoncowySpinner.setAdapter(adapterKoncowy);
    }

    public void dodajNowyOdcinek(View view) {
        String grupaGorska = nazwyGrupGorskich[grupaGorskaSpinner.getSelectedItemPosition()];
        String punktStartowy = nazwyPunktow[punktStartowySpinner.getSelectedItemPosition()];
        String punktKoncowy = nazwyPunktow[punktKoncowySpinner.getSelectedItemPosition()];
        String punktacjaString = punktacjaEditText.getText().toString();
        String punktacjaOdwrotnieString = punktacjaOdwrotnieEditText.getText().toString();

        int punktacja;
        int punktacjaOdwrotnie;
        try {
            punktacja = Integer.parseInt(punktacjaString);
            punktacjaOdwrotnie = Integer.parseInt(punktacjaOdwrotnieString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            punktacja = 0;
            punktacjaOdwrotnie = 0;
        }

        if ((punktacja > 0 || punktacjaOdwrotnie > 0) && punktacja >= 0 && punktacjaOdwrotnie >= 0) {
            Map<String, String> map = new HashMap<>();
            SharedPreferences sharedPreferences = this.getSharedPreferences("pref", Context.MODE_PRIVATE);

            String mail = sharedPreferences.getString("mail", "default_mail");
            String haslo = sharedPreferences.getString("haslo", "default_haslo");
            map.put("mail", mail);
            map.put("haslo", haslo);
            map.put("grupa_gorska", grupaGorska);
            map.put("punktacja", punktacjaString);
            map.put("punktacja_odwrotnie", punktacjaOdwrotnieString);
            map.put("punkt_startowy", punktStartowy);
            map.put("punkt_koncowy", punktKoncowy);

            ServerCaller.add_path(this, this, map, ADD_PATH);
        } else {
            Toast.makeText(this, "Zła punktacja odcinków", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onResponse(JSONObject jsonObject, String callName) {
        if (ADD_PATH.equals(callName)) {
            if (jsonObject == null) {
                Toast.makeText(this, "CONNECTION ERROR", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    String status = jsonObject.getString("status");
                    if ("ADDED".equals(status)) {
                        Toast.makeText(this, "Nowy odcinek został dodany do bazy", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Błąd podczas dodawania odcinka", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Błąd ze zwróconą wartością", Toast.LENGTH_SHORT).show();
                }
            }
        }

    }
}