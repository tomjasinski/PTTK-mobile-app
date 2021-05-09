package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.got.connections.ServerCaller;
import com.example.got.connections.VolleyCallback;
import com.example.got.database.entities.GrupaGorska;
import com.example.got.database.entities.Punkt;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminActivity extends AppCompatActivity implements VolleyCallback {
    private final String GROUPS = "GROUPS";
    private final String POINTS = "POINTS";
    List<GrupaGorska> grupaGorskaList;
    List<Punkt> punktyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setTitle("Admin");
        grupaGorskaList = new ArrayList<>();
        punktyList = new ArrayList<>();
        downloadGrupaGorska();
    }

    public void downloadGrupaGorska() {
        ServerCaller.downloadGrupaGorska(this, this, GROUPS);
    }

    @Override
    public void onResponse(JSONObject jsonObject, String callName) {
        switch (callName) {
            case GROUPS:
                addGroups(jsonObject);
                break;
            case POINTS:
                addPoints(jsonObject);
                break;
            case ServerCaller.ERROR_NAME:
                Toast.makeText(this, "Download error", Toast.LENGTH_SHORT).show();
        }
    }

    private void addGroups(JSONObject jsonObject) {
        try {
            JSONArray resultArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < resultArray.length(); i++) {
                JSONObject groupObject = resultArray.getJSONObject(i);
                int id = groupObject.getInt("id");
                String nazwa = groupObject.getString("nazwa");
                GrupaGorska grupaGorska = new GrupaGorska();
                grupaGorska.id = id;
                grupaGorska.nazwa = nazwa;
                grupaGorskaList.add(grupaGorska);
            }

            //download points from group
            for (int i = 0; i < grupaGorskaList.size(); i++)  {
                ServerCaller.downloadPunkty(this, this, POINTS, grupaGorskaList.get(i).id);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addPoints(JSONObject jsonObject) {
        try {
            JSONArray pointsArray = jsonObject.getJSONArray("points");
            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject pointObject = pointsArray.getJSONObject(i);
                int id = pointObject.getInt("id");
                String nazwa = pointObject.getString("nazwa");
                int grupa_gorska_id = pointObject.getInt("grupa_gorska_id");
                Punkt punkt = new Punkt();
                punkt.id = id;
                punkt.nazwa = nazwa;
                punkt.grupa_gorska_id = grupa_gorska_id;
                punktyList.add(punkt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void openDodajNowyOdcinekActivity(View view) {
        if (grupaGorskaList.size() > 0 && punktyList.size() > 0) {
            HashMap<String, Integer> grupaGorskaMap = new HashMap<>();
            HashMap<Integer, ArrayList<String>> punktyByGrupaGorskaId = new HashMap<>();

            for (int i = 0; i < grupaGorskaList.size(); i++) {
                GrupaGorska grupaGorska = grupaGorskaList.get(i);

                grupaGorskaMap.put(grupaGorska.nazwa, grupaGorska.id);

                ArrayList<String> punktNazwaList = new ArrayList<>();
                for (int j = 0; j < punktyList.size(); j++) {
                    Punkt punkt = punktyList.get(j);
                    if (grupaGorska.id == punkt.grupa_gorska_id) {
                        punktNazwaList.add(punkt.nazwa);
                    }
                }

                punktyByGrupaGorskaId.put(grupaGorska.id, punktNazwaList);
            }


            Intent intent = new Intent(this, DodajNowyOdcinekActivity.class);
            intent.putExtra("grupy", grupaGorskaMap);
            intent.putExtra("punkty", punktyByGrupaGorskaId);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Data not downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}