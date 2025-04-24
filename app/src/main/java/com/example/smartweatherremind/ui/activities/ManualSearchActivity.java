package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartweatherremind.R;

public class ManualSearchActivity extends AppCompatActivity {

    private EditText cityEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_search);

        cityEditText = findViewById(R.id.editTextCity);
        searchButton = findViewById(R.id.buttonSearch);

        searchButton.setOnClickListener(v -> {
            String city = cityEditText.getText().toString().trim();

            if (city.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer une ville", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, WeatherActivity.class);
                intent.putExtra("city", city);
                startActivity(intent);
                finish();
            }
        });
    }
}
