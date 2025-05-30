package com.example.smartweatherremind.reminder.repository;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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

    public static void scheduleReminder(Context context, Reminder reminder) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "Autorisez les alarmes exactes dans les paramètres de l'application.", Toast.LENGTH_LONG).show()
                );

                Intent intent = new Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
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

        Log.d("Reminder", "Alarm scheduled for: " + reminder.title + " at " + triggerAtMillis);

        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
        );
    }
}
