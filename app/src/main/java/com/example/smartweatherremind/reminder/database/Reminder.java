package com.example.smartweatherremind.reminder.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public long timestamp;
    public boolean isAutomatic;
}
