package com.example.smartweatherremind.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.data.model.WeatherResponse;
import com.example.smartweatherremind.data.network.RetrofitInstance;
import com.example.smartweatherremind.data.network.WeatherApiService;
import com.example.smartweatherremind.ui.activities.DashboardActivity;
import com.example.smartweatherremind.utils.Constants;
import com.example.smartweatherremind.utils.PreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManualCityDialogFragment extends DialogFragment {

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null && dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    (int) (requireContext().getResources().getDisplayMetrics().widthPixels * 0.85),
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.GlassDialogTheme);
        dialog.setContentView(R.layout.dialog_manual_search);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText editTextCity = dialog.findViewById(R.id.editTextCity);
        Button buttonSearch = dialog.findViewById(R.id.buttonSearch);

        ProgressBar progressBar = dialog.findViewById(R.id.progressBar);

        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                // Démarrer le chargement
                buttonSearch.setEnabled(false);
                progressBar.setVisibility(View.VISIBLE);

                WeatherApiService service = RetrofitInstance.getApiService();
                Call<WeatherResponse> call = service.getForecast(Constants.WEATHER_API_KEY, city, 1, Constants.LANGUAGE);

                call.enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        buttonSearch.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null) {
                            double lat = response.body().location.lat;
                            double lon = response.body().location.lon;
                            PreferencesHelper.saveLocation(requireContext(), lat, lon);

                            Intent intent = new Intent(requireActivity(), DashboardActivity.class);
                            startActivity(intent);
                            requireActivity().finish();
                            dismiss();
                        } else {
                            safeToast("Ville non reconnue. Réessaye.");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);
                        buttonSearch.setEnabled(true);
                        safeToast("Erreur réseau. Réessaye.");
                    }
                });
            } else {
                safeToast("Veuillez entrer une ville.");
            }
        });


        return dialog;
    }

    private void safeToast(String msg) {
        if (isAdded()) {
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    private Runnable onDismissListener;

    public void setOnDismissListener(Runnable listener) {
        this.onDismissListener = listener;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) onDismissListener.run();
    }

}
