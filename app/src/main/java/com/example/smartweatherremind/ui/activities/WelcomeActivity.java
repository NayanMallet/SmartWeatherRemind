package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smartweatherremind.R;
import com.lottiefiles.dotlottie.core.model.Config;
import com.lottiefiles.dotlottie.core.util.DotLottieSource;
import com.lottiefiles.dotlottie.core.widget.DotLottieAnimation;
import com.dotlottie.dlplayer.Mode;

public class WelcomeActivity extends AppCompatActivity {

    private DotLottieAnimation lottieView;
    private Button btnLocation, btnManual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 1. Récupère les vues APRÈS setContentView()
        lottieView = findViewById(R.id.lottieWelcome);
        btnManual = findViewById(R.id.btnManual);
        btnLocation = findViewById(R.id.btnLocation);

        Config config = new Config.Builder()
                .autoplay(true)
                .speed(1f)
                .loop(true)
                // TODO: Try using ‘weather_anim.json’ local lottie => Animation always displayed
                .source(new DotLottieSource.Url("https://lottie.host/78f6be34-ef93-439c-8d1f-3843fd57c09a/eON7ajOK0h.lottie"))
                .useFrameInterpolation(false)
                .playMode(Mode.FORWARD)
                .build();

        lottieView.load(config);

        // 3. Boutons
        btnLocation.setOnClickListener(view -> {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });

        btnManual.setOnClickListener(view -> {
            Intent intent = new Intent(this, ManualSearchActivity.class);
            startActivity(intent);
        });
    }
}
