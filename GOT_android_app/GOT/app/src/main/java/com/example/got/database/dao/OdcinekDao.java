package com.example.got.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.got.database.entities.Odcinek;

import java.util.List;

@Dao
public interface OdcinekDao {
    @Query("SELECT * FROM Odcinek")
    List<Odcinek> getAll();

    @Insert
    void insertAll(Odcinek... odcinek);
}
