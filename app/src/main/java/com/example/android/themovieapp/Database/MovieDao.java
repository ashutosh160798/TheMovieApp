package com.example.android.themovieapp.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by ashu on 27-06-2018.
 */

@Dao
public interface MovieDao {

    @Insert
    void addMovieFavourite(FavouriteMovie movie);

    @Delete
    void removeMovieFavourite(FavouriteMovie movie);

    @Query("SELECT * FROM favouritemovie WHERE id=:mID")
    FavouriteMovie getMovie(int mID);

    @Query("SELECT image_path FROM favouritemovie")
    LiveData<List<String>> getImgFromDB();

    @Query("SELECT id FROM favouritemovie WHERE image_path=:mImagePath")
    int getID(String mImagePath);
}
