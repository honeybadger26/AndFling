package com.example.andfling.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RoomDao {
    @Query("SELECT * FROM room")
    List<Room> getAll();

    @Insert
    void insertAll(Room... rooms);

    @Delete
    void delete(Room room);
}
