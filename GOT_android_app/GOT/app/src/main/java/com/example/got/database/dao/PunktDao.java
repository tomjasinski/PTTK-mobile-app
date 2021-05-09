package com.example.got.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.got.database.entities.Punkt;

import java.util.List;

@Dao
public interface PunktDao {
    @Query("SELECT * FROM Punkt")
    List<Punkt> getAll();

    @Insert
    void insertAll(Punkt... punkt);
}
