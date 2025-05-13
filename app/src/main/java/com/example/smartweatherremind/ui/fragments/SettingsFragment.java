package com.example.smartweatherremind.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.reminder.repository.ReminderRepository;
import com.example.smartweatherremind.ui.activities.DashboardActivity;
import com.example.smartweatherremind.ui.activities.LocationActivity;
import com.example.smartweatherremind.ui.activities.WelcomeActivity;
import com.example.smartweatherremind.ui.dialogs.ManualCityDialogFragment;
import com.example.smartweatherremind.utils.PreferencesHelper;

public class SettingsFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button btnEditLocation = view.findViewById(R.id.btnEditLocation);
        Button btnClearData = view.findViewById(R.id.btnClearData);

        btnEditLocation.setOnClickListener(v -> showEditLocationOptions());

        btnClearData.setOnClickListener(v -> {
            PreferencesHelper.clearLocation(requireContext());
            new ReminderRepository(requireContext()).deleteAll(() -> {
                Intent intent = new Intent(requireContext(), WelcomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        });

        return view;
    }

    private void showEditLocationOptions() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Choisir la méthode de localisation")
                .setItems(new CharSequence[]{"Ma position", "Entrer manuellement"}, (dialog, which) -> {
                    if (which == 0) {
                        Intent intent = new Intent(requireContext(), LocationActivity.class);
                        startActivity(intent);
                    } else {
                        ManualCityDialogFragment dialogFragment = new ManualCityDialogFragment();
                        dialogFragment.setOnDismissListener(() -> {
                            Intent intent = new Intent(requireContext(), DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        });
                        dialogFragment.show(getParentFragmentManager(), "manualCity");
                    }
                })
                .show();
    }
}
