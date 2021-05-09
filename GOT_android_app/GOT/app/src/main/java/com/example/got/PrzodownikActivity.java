package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class PrzodownikActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_przodownik);

        setTitle("Przodownik");
    }

    public void openPotwierdzenieTrasyLista(View view) {
        Intent intent = new Intent(this, PotwierdzenieTrasyListaWnioskowActivity.class);
        startActivity(intent);
    }
}