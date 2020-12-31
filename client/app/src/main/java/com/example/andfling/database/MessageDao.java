package com.example.andfling.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message WHERE roomId = :roomId")
    LiveData<List<Message>> getAll(int roomId);

    @Insert
    void insertAll(Message... messages);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM message")
    void deleteAll();
}
