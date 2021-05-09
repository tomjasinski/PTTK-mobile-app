package com.example.got.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Punkt {
    @PrimaryKey
    public int id;

    public String nazwa;

    public int grupa_gorska_id;
}
