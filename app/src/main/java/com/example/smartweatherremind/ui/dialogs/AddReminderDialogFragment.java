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
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.repository.ReminderRepository;
import com.example.smartweatherremind.ui.activities.DashboardActivity;
import com.example.smartweatherremind.ui.fragments.RemindersFragment;

import java.util.Calendar;

public class AddReminderDialogFragment extends DialogFragment {

    private long selectedTimestamp = -1;

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
        Button buttonPickDate = dialog.findViewById(R.id.buttonPickDate);
        Button buttonSave = dialog.findViewById(R.id.buttonSaveReminder);

        buttonPickDate.setOnClickListener(v -> openDatePicker());

        buttonSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString().trim();
            if (!reminderText.isEmpty() && selectedTimestamp != -1) {
                Reminder reminder = new Reminder();
                reminder.title = reminderText;
                reminder.isAutomatic = false;
                reminder.timestamp = selectedTimestamp;

                new ReminderRepository(requireContext()).insert(reminder);
                requireActivity().runOnUiThread(() -> {
                    if (getActivity() instanceof DashboardActivity) {
                        Fragment currentFragment = ((DashboardActivity) getActivity()).getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (currentFragment instanceof RemindersFragment) {
                            ((RemindersFragment) currentFragment).refreshReminders();
                        }
                    }
                });

                dismiss();
            }
        });

        return dialog;
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth, 0, 0);
            selectedTimestamp = calendar.getTimeInMillis();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }
}
