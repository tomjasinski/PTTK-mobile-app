package com.example.got;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openZalogujActivity(View view) {
        Intent intent = new Intent(this, ZalogujActivity.class);
        startActivity(intent);
    }

    public void openRejestracjaActivity(View view) {
        Intent intent = new Intent(this, RejestracjaActivity.class);
        startActivity(intent);
    }
}