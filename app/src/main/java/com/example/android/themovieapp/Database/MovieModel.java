package com.example.android.themovieapp.Database;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by ashu on 29-06-2018.
 */

public class MovieModel extends AndroidViewModel {
    private final AppDatabase database;
    private LiveData<List<String>> favoriteMoviesImageList;


    public MovieModel(@NonNull Application application) {
        super(application);
        database = AppDatabase.getDatabase(getApplication());
    }

    public LiveData<List<String>> getFavoriteMovies() {
        if (favoriteMoviesImageList == null) {
            favoriteMoviesImageList = new MutableLiveData<>();
            getFavoritesFromDatabase();
        }
        return favoriteMoviesImageList;
    }

    private void getFavoritesFromDatabase() {
        favoriteMoviesImageList = database.movieDao().getImgFromDB();
    }
}
