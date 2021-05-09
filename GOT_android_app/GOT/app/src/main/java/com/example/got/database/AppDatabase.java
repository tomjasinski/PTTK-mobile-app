package com.example.got.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.got.database.dao.GrupaGorskaDao;
import com.example.got.database.dao.OdcinekDao;
import com.example.got.database.dao.PunktDao;
import com.example.got.database.entities.GrupaGorska;
import com.example.got.database.entities.Odcinek;
import com.example.got.database.entities.Punkt;

@Database(entities = {GrupaGorska.class, Odcinek.class, Punkt.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract GrupaGorskaDao grupaGorskaDao();
    public abstract OdcinekDao odcinekDao();
    public abstract PunktDao punktDao();
}
