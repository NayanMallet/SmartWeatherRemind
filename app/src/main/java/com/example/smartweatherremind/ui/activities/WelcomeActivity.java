package com.example.smartweatherremind.ui.activities;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.smartweatherremind.R;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION_CHECK", "Notification permission GRANTED");
            } else {
                Log.d("PERMISSION_CHECK", "Notification permission DENIED");
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Notification permission (API 33+)
        onRequestPermissionsResult(1, new String[]{Manifest.permission.POST_NOTIFICATIONS}, new int[]{PackageManager.PERMISSION_GRANTED});
        Log.d("PERMISSION_CHECK", "Demande de permission POST_NOTIFICATIONS");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        1
                );
            }
        }

        // 2. Create channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Rappels",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Canal pour les notifications de rappels");

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }

        // 3. Maintenant, tu peux rediriger si localisation déjà connue
        if (PreferencesHelper.hasSavedLocation(this)) {
            startActivity(new Intent(this, DashboardActivity.class));
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
        // 3. Boutons
        btnLocation.setOnClickListener(view -> {
            Intent intent = new Intent(this, LocationActivity.class);
            startActivity(intent);
        });

        btnManual.setOnClickListener(view -> {
            new ManualCityDialogFragment().show(getSupportFragmentManager(), "manualCity");
        });
    }
}