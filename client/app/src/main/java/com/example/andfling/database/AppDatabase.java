package com.example.andfling.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Message.class, com.example.andfling.database.Room.class}, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    private static final String DB_NAME = "andfling_db";
    private static AppDatabase instance;

    public abstract MessageDao messageDao();
    public abstract RoomDao roomDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                DB_NAME).fallbackToDestructiveMigration().build();
        }
        return instance;
    }
}
