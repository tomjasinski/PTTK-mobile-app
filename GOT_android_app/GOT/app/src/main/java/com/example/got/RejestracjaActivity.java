package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.got.connections.ServerCaller;
import com.example.got.connections.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class RejestracjaActivity extends AppCompatActivity implements VolleyCallback {
    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;
    private final String CALL_NAME = "REGISTER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejestracja);
        setTitle("Rejestracja");

        dateView = (TextView) findViewById(R.id.tv_data_urodzenia_rejestracja);
        calendar = Calendar.getInstance();

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month+1, day);
    }


    public void register(View view) {
        TextView textViewWynik = findViewById(R.id.tv_wynik_rejestracja);
        EditText editTextMail = findViewById(R.id.et_mail_rejestracja);
        EditText editTextHaslo = findViewById(R.id.et_haslo_rejestracja);
        EditText editTextImie = findViewById(R.id.et_imie_rejestracja);
        EditText editTextNazwisko = findViewById(R.id.et_nazwisko_rejestracja);
        TextView textViewDataUrodzenia = findViewById(R.id.tv_data_urodzenia_rejestracja);

        textViewWynik.setText("");
        String mail = editTextMail.getText().toString();
        String haslo = editTextHaslo.getText().toString();
        String imie = editTextImie.getText().toString();
        String nazwisko = editTextNazwisko.getText().toString();
        String dataUrodzenia = textViewDataUrodzenia.getText().toString();

        Map<String, String> map = new HashMap<>();
        map.put("mail", mail);
        map.put("haslo", haslo);
        map.put("imie", imie);
        map.put("nazwisko", nazwisko);
        map.put("data_urodzenia", dataUrodzenia);

        ServerCaller.register(this, this, map, CALL_NAME);
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = (arg0, arg1, arg2, arg3) -> {
        // TODO Auto-generated method stub
        // arg1 = year
        // arg2 = month
        // arg3 = day
        showDate(arg1, arg2+1, arg3);
    };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(year).append("-")
                .append(month).append("-").append(day));
    }

    @Override
    public void onResponse(JSONObject jsonObject, String callName) {
        switch (callName) {
            case CALL_NAME:
                handleRegisterResponse(jsonObject);
                break;
            case ServerCaller.ERROR_NAME:
                Toast.makeText(this, "Connection error", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void handleRegisterResponse(JSONObject jsonObject) {
        try {
            int id = jsonObject.getInt("id");
            String description = jsonObject.getString("description");

            if (id > 0 && "OK".equals(description)) {
                Intent intent = new Intent(this, PoRejestracjiActivity.class);
                intent.putExtra("info", "Konto zostało założone");
                startActivity(intent);
            } else if (id == -1 && "login taken".equals(description)) {
                Intent intent = new Intent(this, PoRejestracjiActivity.class);
                intent.putExtra("info", "Konto już istnieje");
                startActivity(intent);
            } else if (id == -1 && "invalid data".equals(description)) {
                Toast.makeText(this, "Podane dane są nieprawidłowe", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}