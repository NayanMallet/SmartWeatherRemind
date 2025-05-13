package com.example.smartweatherremind.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.utils.DialogUtils;
import com.example.smartweatherremind.utils.PreferencesHelper;
import com.example.smartweatherremind.ui.dialogs.ManualCityDialogFragment;
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

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                DialogUtils.showExitDialog(WelcomeActivity.this);
            }
        });
        if (PreferencesHelper.hasSavedLocation(this)) {
            Intent intent = new Intent(this, DashboardActivity.class); // Remplace par le nom réel si différent
            startActivity(intent);
            finish();
            return;
        }

        setContentView(R.layout.activity_welcome);

        lottieView = findViewById(R.id.lottieWelcome);
        btnLocation = findViewById(R.id.btnLocation);
        btnManual = findViewById(R.id.btnManual);

        Config config = new Config.Builder()
                .autoplay(true)
                .speed(1f)
                .loop(true)
                .source(new DotLottieSource.Url("https://lottie.host/78f6be34-ef93-439c-8d1f-3843fd57c09a/eON7ajOK0h.lottie"))
                .useFrameInterpolation(false)
                .playMode(Mode.FORWARD)
                .build();

        lottieView.load(config);

        lottieView.setScaleX(1.1f);
        lottieView.setScaleY(1.1f);
        btnLocation.setOnClickListener(view -> {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });

        btnManual.setOnClickListener(view -> {
            new ManualCityDialogFragment().show(getSupportFragmentManager(), "manualCity");
        });
    }
}