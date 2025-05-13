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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        remindersContainer = view.findViewById(R.id.remindersContainer);

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
        repository.getAllReminders(reminders -> requireActivity().runOnUiThread(() -> {
            remindersContainer.removeAllViews();
            if (reminders.isEmpty()) {
                TextView emptyMessage = new TextView(requireContext());
                emptyMessage.setText("Aucun rappel enregistré.");
                emptyMessage.setTextColor(getResources().getColor(R.color.cloud_white));
                emptyMessage.setTextSize(18);
                emptyMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                // Centrage dans le conteneur parent
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.gravity = android.view.Gravity.CENTER;
                emptyMessage.setLayoutParams(params);

                remindersContainer.addView(emptyMessage);
            } else {
                reminders.sort((r1, r2) -> Long.compare(r1.timestamp, r2.timestamp));
                for (Reminder reminder : reminders) {
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

        String formattedDate = new SimpleDateFormat("dd/MM/yyyy").format(new Date(reminder.timestamp));
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
