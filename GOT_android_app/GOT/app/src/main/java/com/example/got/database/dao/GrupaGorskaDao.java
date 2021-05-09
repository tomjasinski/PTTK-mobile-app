package com.example.got.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.got.database.entities.GrupaGorska;

import java.util.List;

@Dao
public interface GrupaGorskaDao {
    @Query("SELECT * FROM GrupaGorska")
    List<GrupaGorska> getAll();

    @Insert
    void insertAll(GrupaGorska... grupaGorska);
}
