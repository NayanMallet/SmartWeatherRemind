package com.example.smartweatherremind.reminder.ui;

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

public class AddReminderDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_reminder);

        EditText editTextReminder = dialog.findViewById(R.id.editTextReminder);
        Button buttonSave = dialog.findViewById(R.id.buttonSaveReminder);

        buttonSave.setOnClickListener(v -> {
            String reminderText = editTextReminder.getText().toString().trim();
            if (!reminderText.isEmpty()) {
                Reminder reminder = new Reminder();
                reminder.title = reminderText;
                reminder.isAutomatic = false;
                reminder.time = "00:00";

                new ReminderRepository(requireContext()).insert(reminder);
                dismiss();
            }
        });

        return dialog;
    }
}
