package com.example.smartweatherremind.reminder.repository;

import android.content.Context;

import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.database.ReminderDatabase;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ReminderRepository {

    private final ReminderDatabase db;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ReminderRepository(Context context) {
        db = ReminderDatabase.getInstance(context);
    }

    public void insert(Reminder reminder) {
        executor.execute(() -> db.reminderDao().insert(reminder));
    }

    public void delete(Reminder reminder) {
        executor.execute(() -> db.reminderDao().delete(reminder));
    }

    public void update(Reminder reminder) {
        executor.execute(() -> db.reminderDao().update(reminder));
    }

    public interface Callback {
        void onResult(List<Reminder> reminders);
    }

    public void getAllReminders(Callback callback) {
        executor.execute(() -> {
            List<Reminder> list = db.reminderDao().getAllReminders();
            callback.onResult(list);
        });
    }
}
