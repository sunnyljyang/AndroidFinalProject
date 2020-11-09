package com.example.androidfinalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button btnTicketMaster = findViewById(R.id.btnTicketMaster);
        final Button btnRecipeSearch = findViewById(R.id.btnRecipeSearch);
        final Button btnCovid19Data = findViewById(R.id.btnCovid19Data);
        final Button btnAudioDatabase = findViewById(R.id.btnAudioDatabase);
        btnTicketMaster.setOnClickListener(v->startActivity(new Intent(MainActivity.this, TicketMasterActivity.class)));
        btnRecipeSearch.setOnClickListener(v->startActivity(new Intent(MainActivity.this, RecipeSearchActivity.class)));
        btnCovid19Data.setOnClickListener(v->startActivity(new Intent(MainActivity.this, Covid19DataActivity.class)));
        btnAudioDatabase.setOnClickListener(v->startActivity(new Intent(MainActivity.this, AudioDatabaseActivity.class)));


    }
}