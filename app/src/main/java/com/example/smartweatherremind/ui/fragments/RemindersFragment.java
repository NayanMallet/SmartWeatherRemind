package com.example.smartweatherremind.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.repository.ReminderRepository;
import com.example.smartweatherremind.reminder.ui.AddReminderDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class RemindersFragment extends Fragment {

    private LinearLayout remindersContainer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        remindersContainer = view.findViewById(R.id.remindersContainer);

        FloatingActionButton addReminderButton = view.findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(v -> new AddReminderDialogFragment().show(getParentFragmentManager(), "addReminder"));

        loadReminders();

        return view;
    }

    private void loadReminders() {
        ReminderRepository repository = new ReminderRepository(requireContext());
        repository.getAllReminders(reminders -> {
            requireActivity().runOnUiThread(() -> {
                remindersContainer.removeAllViews();
                if (reminders.isEmpty()) {
                    addReminderView("Aucun rappel enregistré.");
                } else {
                    for (Reminder reminder : reminders) {
                        addReminderView(reminder.title);
                    }
                }
            });
        });
    }


    private void addReminderView(String text) {
        View reminderView = LayoutInflater.from(requireContext())
                .inflate(R.layout.rappel_item, remindersContainer, false);

        TextView reminderTextView = reminderView.findViewById(R.id.reminderTextView);
        reminderTextView.setText(text);

        remindersContainer.addView(reminderView);
    }

}
