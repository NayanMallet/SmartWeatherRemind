package com.example.smartweatherremind.ui.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.repository.ReminderRepository;
import com.example.smartweatherremind.ui.fragments.RemindersFragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddReminderDialogFragment extends DialogFragment {

    private long selectedTimestamp = -1;
    private Button buttonPickDate;

    private OnReminderAddedListener callback;

    public void setOnReminderAddedListener(OnReminderAddedListener callback) {
        this.callback = callback;
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext(), R.style.GlassDialogTheme);
        dialog.setContentView(R.layout.dialog_add_reminder);

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }

        EditText editTextReminder = dialog.findViewById(R.id.editTextReminder);
        buttonPickDate = dialog.findViewById(R.id.buttonPickDate);
        Button buttonSave = dialog.findViewById(R.id.buttonSaveReminder);

        buttonPickDate.setOnClickListener(v -> openDatePicker());

        buttonSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString().trim();
            if (!reminderText.isEmpty() && selectedTimestamp != -1) {
                Reminder reminder = new Reminder();
                reminder.title = reminderText;
                reminder.isAutomatic = false;
                reminder.timestamp = selectedTimestamp;

                new ReminderRepository(requireContext()).insert(reminder, () -> {
                    if (callback != null) {
                        callback.onReminderAdded();
                    }
                    dismiss();
                });
            }
        });

        return dialog;
    }

    public interface OnReminderAddedListener {
        void onReminderAdded();
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth, 0, 0);
            selectedTimestamp = calendar.getTimeInMillis();

            // Mise à jour du texte du bouton avec la date sélectionnée
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    .format(calendar.getTime());
            buttonPickDate.setText(formattedDate);

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
