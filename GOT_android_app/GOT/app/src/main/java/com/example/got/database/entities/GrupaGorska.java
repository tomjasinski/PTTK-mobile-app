package com.example.got.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class GrupaGorska {
    @PrimaryKey
    public int id;

    public String nazwa;
}
