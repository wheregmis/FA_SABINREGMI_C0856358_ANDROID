package com.example.fa_sabinregmi_c0856358_android.database;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fa_sabinregmi_c0856358_android.model.Place;

import java.util.List;

@Dao
public interface PlaceDao {

    @Insert
    void insert(Place place);

    @Update
    void update(Place place);

    @Delete
    void delete(Place place);

    @Query("SELECT * FROM place")
    List<Place> getAllPlaces();

    @Query("SELECT * FROM place WHERE id IN (:id)")
    Place getProductById(int id);

    @Query("SELECT * FROM place WHERE isFav = 1")
    List<Place> getAllFav();
}
