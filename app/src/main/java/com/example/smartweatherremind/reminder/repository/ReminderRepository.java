package com.example.smartweatherremind.reminder.repository;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.database.ReminderDatabase;
import com.example.smartweatherremind.reminder.model.ReminderDao;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import java.util.List;

public class ReminderRepository {

    private final ReminderDatabase db;
    private final ReminderDao reminderDao;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ReminderRepository(Context context) {
        db = ReminderDatabase.getInstance(context);
        reminderDao = db.reminderDao();
    }


    public interface SimpleCallback {
        void onComplete();
    }

    public void insert(Reminder reminder, SimpleCallback callback) {
        executor.execute(() -> {
            db.reminderDao().insert(reminder);
            if (callback != null) callback.onComplete();
        });
    }

    public void delete(Reminder reminder, Runnable callback) {
        executor.execute(() -> {
            reminderDao.delete(reminder);
            if (callback != null) callback.run();
        });
    }


    public void update(Reminder reminder, Runnable callback) {
        executor.execute(() -> {
            db.reminderDao().update(reminder);
            if (callback != null) callback.run();
        });
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

    public void deleteAll(Runnable onComplete) {
        executor.execute(() -> {
            reminderDao.deleteAll();
            if (onComplete != null) {
                new Handler(Looper.getMainLooper()).post(onComplete);
            }
        });
    }

}
