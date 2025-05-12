package com.example.smartweatherremind.reminder.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public String time; // format "HH:mm"
    public boolean isAutomatic; // true = météo, false = perso
}
