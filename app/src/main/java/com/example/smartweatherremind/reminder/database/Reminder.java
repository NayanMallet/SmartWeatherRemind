package com.example.smartweatherremind.reminder.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "reminders")
public class Reminder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;
    public long timestamp; // Stockage de la date sous forme de timestamp (millisecondes)
    public boolean isAutomatic; // true = météo, false = perso
}
