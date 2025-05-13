package com.example.smartweatherremind.reminder.repository;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.smartweatherremind.reminder.database.Reminder;
import com.example.smartweatherremind.reminder.database.ReminderDatabase;
import com.example.smartweatherremind.reminder.model.ReminderDao;
import com.example.smartweatherremind.reminder.receiver.ReminderReceiver;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import java.util.List;

public class ReminderRepository {

    private final ReminderDatabase db;
    private final ReminderDao reminderDao; // ← tu l'ajoutes ici
    private final Executor executor = Executors.newSingleThreadExecutor();

    public ReminderRepository(Context context) {
        db = ReminderDatabase.getInstance(context);
        reminderDao = db.reminderDao(); // ← et ici tu l'initialises
    }

    // le reste reste inchangé


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

    public static void scheduleReminder(Context context, Reminder reminder) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("Reminder", "Impossible de planifier une alarme exacte : permission refusée dans les paramètres.");
                Toast.makeText(context, "Veuillez autoriser les alarmes exactes dans les paramètres.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        Intent intent = new Intent(context, ReminderReceiver.class);
        intent.putExtra("title", reminder.title);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                reminder.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        long triggerAtMillis = reminder.timestamp;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("Reminder", "Impossible de planifier une alarme exacte : permission refusée dans les paramètres.");
                return;
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }
}
