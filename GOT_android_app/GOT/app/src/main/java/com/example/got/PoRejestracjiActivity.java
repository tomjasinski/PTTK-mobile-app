package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class PoRejestracjiActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_po_rejestracji);

        TextView textViewInfo = findViewById(R.id.tv_info_po_rejestracji);
        Bundle extras = getIntent().getExtras();
        String info = extras.getString("info");
        textViewInfo.setText(info);
    }



    public void openZalogujActivityPoRejestracji(View view) {
        Intent intent = new Intent(this, ZalogujActivity.class);
        startActivity(intent);
    }
}