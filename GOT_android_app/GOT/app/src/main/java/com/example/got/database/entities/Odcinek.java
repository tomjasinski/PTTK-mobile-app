package com.example.got.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Odcinek {
    @PrimaryKey
    public int id;

    public int punktacja;
    public int punktacja_odwrotnie;
    public int punkt_startowy;
    public int punkt_koncowy;
}
