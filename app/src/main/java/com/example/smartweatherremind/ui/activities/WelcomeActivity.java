package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartweatherremind.R;

public class WelcomeActivity extends AppCompatActivity {

    private Button btnLocation, btnManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        btnLocation = findViewById(R.id.btnLocation);
        btnManual = findViewById(R.id.btnManual);

        btnLocation.setOnClickListener(view -> {
            // Lancer localisation (à implémenter)
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });

        btnManual.setOnClickListener(view -> {
            // Lancer saisie manuelle (à implémenter)
            Intent intent = new Intent(this, ManualSearchActivity.class);
            startActivity(intent);
        });
    }
}
