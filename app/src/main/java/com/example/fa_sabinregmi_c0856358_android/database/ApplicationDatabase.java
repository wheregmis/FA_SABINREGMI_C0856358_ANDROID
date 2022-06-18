package com.example.fa_sabinregmi_c0856358_android.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.fa_sabinregmi_c0856358_android.model.Place;
@Database(entities = {Place.class}, version = 1, exportSchema = false)
public abstract class ApplicationDatabase extends RoomDatabase {
    public abstract PlaceDao placeDao();
}

