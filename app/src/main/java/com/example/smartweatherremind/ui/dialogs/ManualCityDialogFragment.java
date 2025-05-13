package com.example.smartweatherremind.ui.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.ui.fragments.HomeFragment;

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

        buttonSearch.setOnClickListener(v -> {
            String city = editTextCity.getText().toString().trim();
            if (!city.isEmpty()) {
                Intent intent = new Intent(requireActivity(), HomeFragment.class);
                intent.putExtra("city", city);
                startActivity(intent);
                dismiss();
            } else {
                Toast.makeText(requireContext(), "Veuillez entrer une ville.", Toast.LENGTH_SHORT).show();
            }
        });

        return dialog;
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
