package com.example.smartweatherremind.reminder.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.example.smartweatherremind.reminder.database.Reminder;
import java.util.List;

@Dao
public interface ReminderDao {

    @Query("SELECT * FROM reminders ORDER BY timestamp ASC")
    List<Reminder> getAllReminders();

    @Insert
    void insert(Reminder reminder);

    @Update
    void update(Reminder reminder);

    @Delete
    void delete(Reminder reminder);
}
