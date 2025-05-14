package com.example.smartweatherremind.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.smartweatherremind.R;
import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.repository.ReminderRepository;
import com.example.smartweatherremind.ui.dialogs.AddReminderDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RemindersFragment extends Fragment {

    private LinearLayout remindersContainer;
    private TextView emptyTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        remindersContainer = view.findViewById(R.id.remindersContainer);
        emptyTextView = view.findViewById(R.id.emptyRemindersText);

        FloatingActionButton addReminderButton = view.findViewById(R.id.addReminderButton);
        addReminderButton.setOnClickListener(v -> {
            AddReminderDialogFragment dialogFragment = new AddReminderDialogFragment();
            dialogFragment.setOnReminderAddedListener(this::refreshReminders);
            dialogFragment.show(getParentFragmentManager(), "AddReminderDialog");
        });



        refreshReminders();

        return view;
    }

    public void refreshReminders() {
        ReminderRepository repository = new ReminderRepository(requireContext());
        long now = System.currentTimeMillis();

        repository.getAllReminders(reminders -> requireActivity().runOnUiThread(() -> {
            remindersContainer.removeAllViews();

            for (Reminder reminder : reminders) {
                if (reminder.timestamp < now) {
                    repository.delete(reminder, null);
                }
            }

            List<Reminder> upcomingReminders = reminders.stream()
                    .filter(r -> r.timestamp >= now)
                    .sorted((r1, r2) -> Long.compare(r1.timestamp, r2.timestamp))
                    .toList();

            if (upcomingReminders.isEmpty()) {
                emptyTextView.setVisibility(View.VISIBLE);
                remindersContainer.setVisibility(View.GONE);
            } else {
                emptyTextView.setVisibility(View.GONE);
                remindersContainer.setVisibility(View.VISIBLE);

                for (Reminder reminder : upcomingReminders) {
                    addReminderView(reminder);
                }
            }
        }));
    }

    private void deleteReminder(Reminder reminder) {
        new ReminderRepository(requireContext()).delete(reminder, this::refreshReminders);
    }

    private void openEditDialog(Reminder reminder) {
        AddReminderDialogFragment dialog = new AddReminderDialogFragment();
        dialog.setReminderToEdit(reminder);
        dialog.setOnReminderAddedListener(this::refreshReminders);
        dialog.show(getParentFragmentManager(), "EditReminderDialog");
    }




    private void addReminderView(Reminder reminder) {
        View reminderView = LayoutInflater.from(requireContext())
                .inflate(R.layout.rappel_item, remindersContainer, false);

        TextView reminderTextView = reminderView.findViewById(R.id.reminderTextView);
        ImageView menuButton = reminderView.findViewById(R.id.menuButton);

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date(reminder.timestamp));
        reminderTextView.setText(reminder.title + " - " + formattedDate);

        menuButton.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(requireContext(), v);
            popup.getMenu().add("Modifier");
            popup.getMenu().add("Supprimer");

            popup.setOnMenuItemClickListener(item -> {
                String title = item.getTitle().toString();
                if (title.equals("Modifier")) {
                    openEditDialog(reminder);
                } else if (title.equals("Supprimer")) {
                    deleteReminder(reminder);
                }
                return true;
            });

            popup.show();
        });

        remindersContainer.addView(reminderView);
    }



}
