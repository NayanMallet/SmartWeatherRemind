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

    private Reminder reminderToEdit = null;

    public void setReminderToEdit(Reminder reminder) {
        this.reminderToEdit = reminder;
    }


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

        // ⚠️ Pré-remplissage si modification
        if (reminderToEdit != null) {
            editTextReminder.setText(reminderToEdit.title);
            selectedTimestamp = reminderToEdit.timestamp;
            String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    .format(new java.util.Date(reminderToEdit.timestamp));
            buttonPickDate.setText(formattedDate);
        }

        buttonPickDate.setOnClickListener(v -> openDatePicker());

        buttonSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString().trim();
            if (!reminderText.isEmpty() && selectedTimestamp != -1) {
                ReminderRepository repo = new ReminderRepository(requireContext());

                if (reminderToEdit != null) {
                    reminderToEdit.title = reminderText;
                    reminderToEdit.timestamp = selectedTimestamp;
                    repo.update(reminderToEdit, () -> {
                        if (callback != null) callback.onReminderAdded();
                        dismiss();
                    });
                } else {
                    Reminder reminder = new Reminder();
                    reminder.title = reminderText;
                    reminder.timestamp = selectedTimestamp;
                    reminder.isAutomatic = false;
                    repo.insert(reminder, () -> {
                        if (callback != null) callback.onReminderAdded();
                        dismiss();
                    });
                }
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
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Ensuite, on demande l’heure
            new android.app.TimePickerDialog(requireContext(), (timeView, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                calendar.set(Calendar.SECOND, 0);

                selectedTimestamp = calendar.getTimeInMillis();

                String formatted = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        .format(calendar.getTime());
                buttonPickDate.setText(formatted);

            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}
