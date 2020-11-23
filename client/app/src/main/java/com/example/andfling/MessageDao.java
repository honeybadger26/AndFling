package com.example.andfling;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM message")
    LiveData<List<Message>> getAll();

    @Insert
    void insertAll(Message... messages);

    @Delete
    void delete(Message message);

    @Query("DELETE FROM message")
    void deleteAll();
}
