package com.example.android.themovieapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.themovieapp.Database.AppDatabase;
import com.example.android.themovieapp.Database.FavouriteMovie;
import com.example.android.themovieapp.Database.MovieModel;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    public static ArrayList<FavouriteMovie> movieObjectList;
    public static FavouriteMovie movieObject;
    private static String sortType = "popular";
    private ArrayList<String> img;
    private GridView mGridView;
    private TextView mTextView;
    private Spinner spinner;
    private int mScrollPosition = -1;
    private int view = -1;
    private final String mScrollPositionKey = "scroll_key";
    private MyExecutor mExecutor;
    private MovieModel mViewModel;
    private int x;
    private ImageAdapter imageAdapter;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mGridView = findViewById(R.id.gridView1);
        mTextView = findViewById(R.id.text_view);
        img = new ArrayList<>();
        mViewModel = ViewModelProviders.of(MainActivity.this).get(MovieModel.class);
        movieObjectList = new ArrayList<>();
        mExecutor = new MyExecutor();
        appDatabase = AppDatabase.getDatabase(this);
        imageAdapter = new ImageAdapter(this, R.layout.activity_main, img);
        mGridView.setAdapter(imageAdapter);

    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        mTextView.setVisibility(View.GONE);
        mViewModel.getFavoriteMovies().removeObservers(MainActivity.this);
        if (i == 0) {
            x = 0;
            sortType = "popular";
            mGridView = findViewById(R.id.gridView1);
            img = new ArrayList<>();
            movieObjectList = new ArrayList<>();


            imageAdapter = new ImageAdapter(this, R.layout.activity_main, img);
            mGridView.setAdapter(imageAdapter);
            mGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    movieObject = movieObjectList.get(i);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            });


            DownloadContent downloadContent = new DownloadContent();

            downloadContent.execute(
                    NetworkUtil.getMoviesListURL(sortType).toString());
        } else if (i == 1) {
            x = 1;
            sortType = "top_rated";
            mGridView = findViewById(R.id.gridView1);
            img = new ArrayList<>();
            movieObjectList = new ArrayList<>();


            imageAdapter = new ImageAdapter(this, R.layout.activity_main, img);
            mGridView.setAdapter(imageAdapter);
            mGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    movieObject = movieObjectList.get(i);
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            });


            DownloadContent downloadContent = new DownloadContent();

            downloadContent.execute(NetworkUtil.getMoviesListURL(sortType).toString());
            restoreScrollPosition();

        } else {
            x = 2;
            mGridView = findViewById(R.id.gridView1);
            mViewModel.getFavoriteMovies().observe(MainActivity.this, new Observer<List<String>>() {
                @Override
                public void onChanged(@Nullable List<String> imgs) {
                    img = new ArrayList<>();
                    img.addAll(imgs);
                    if (imgs.size() == 0) {
                        mTextView.setVisibility(View.VISIBLE);
                    } else {
                        mTextView.setVisibility(View.GONE);
                    }
                    imageAdapter = new ImageAdapter(MainActivity.this, R.layout.activity_main, img);

                    mGridView.setAdapter(imageAdapter);
                    imageAdapter.notifyDataSetChanged();
                    mGridView.setOnItemClickListener(new GridView.OnItemClickListener() {
                        int id;

                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                            mExecutor.execute(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                    id = appDatabase.movieDao().getID(img.get(i));
                                    movieObject = appDatabase.movieDao().getMovie(id);
                                    startActivity(intent);
                                }
                            });
                        }
                    });
                }
            });


        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.spinner, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        spinner = (Spinner) item.getActionView();
        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.SortBy, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
        if (view != -1) {
            spinner.setSelection(view);
        }
        return true;
    }

    private class DownloadContent extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = NetworkUtil.getJSON(NetworkUtil.getMoviesListURL(sortType));
                return response;
            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (response != null) {
                ArrayList<String> in = NetworkUtil.loadMovieList(response);
                img.addAll(in);
                imageAdapter.notifyDataSetChanged();
                restoreScrollPosition();

            } else {
                Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("view", x);
        outState.putInt(mScrollPositionKey, mGridView.getFirstVisiblePosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        view = savedInstanceState.getInt("view");
        mScrollPosition = savedInstanceState.getInt(mScrollPositionKey);
    }

    private void restoreScrollPosition() {
        if (mScrollPosition != -1)
            mGridView.setSelection(mScrollPosition);

    }


}
