package com.example.andfling.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "message",
    foreignKeys = @ForeignKey(
        entity = Room.class,
        parentColumns = "id",
        childColumns = "roomId"
    )
)
public class Message {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "date")
    public String date;

    @ColumnInfo(name = "contents")
    public String contents;

    @ColumnInfo(name = "roomId", defaultValue = "1")
    public int roomId;
}
