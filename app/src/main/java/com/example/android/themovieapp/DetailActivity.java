package com.example.android.themovieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.themovieapp.Database.AppDatabase;
import com.example.android.themovieapp.Database.FavouriteMovie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.example.android.themovieapp.MainActivity.movieObject;

public class DetailActivity extends AppCompatActivity {

    public static ArrayList<TrailerObject> trailerObjectArrayList;
    public static ArrayList<ReviewsObject> reviewsObjectArrayList;
    private RecyclerView trailersRv;
    private int movieId;
    private TextView synopsis;
    private MyExecutor mExecutor;
    private String mScrollPositionKey;
    private TextView rating;
    private TextView date;
    private TextView name;
    private TextView reviewNotAvailable;
    private ImageView poster;
    private ScrollView mScrollView;
    private int[] mScrollPosition;
    private TrailerAdapter trailerAdapter;
    private FavouriteMovie favouriteMovie;
    private ReviewsAdapter reviewsAdapter;
    private RecyclerView reviewsRv;
    private CheckBox favoriteCheckBox;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        poster = findViewById(R.id.image_poster);
        mExecutor = new MyExecutor();
        appDatabase = AppDatabase.getDatabase(this);
        reviewNotAvailable = findViewById(R.id.review_not_available);
        name = findViewById(R.id.title);
        favoriteCheckBox = findViewById(R.id.checkBox);
        date = findViewById(R.id.release_date);
        rating = findViewById(R.id.user_rating);
        synopsis = findViewById(R.id.synopsis);
        trailersRv = findViewById(R.id.trailer_rv);
        trailerObjectArrayList = new ArrayList<>();
        mScrollView = findViewById(R.id.scroll_view);
        reviewsObjectArrayList = new ArrayList<>();
        reviewsRv = findViewById(R.id.reviews_rv);


        if (!movieObject.getImagePath().contains("image")) {
            Picasso.with(this).load(IntentConstant.IMAGE_URL + IntentConstant.IMAGE_SIZE_185 + movieObject.getImagePath()).resize(500, 750).into(poster);
        } else {
            Picasso.with(this).load(movieObject.getImagePath()).resize(500, 750).into(poster);

        }
        movieId = movieObject.getMovieId();
        name.setText(movieObject.getMovieTitle());
        if (movieObject.getRelease_date().length() != 0) {
            date.setText(movieObject.getRelease_date().substring(0, 10));
        }
        rating.setText(movieObject.getVote_average());
        synopsis.setText(movieObject.getOverview());

        mExecutor.execute(new Runnable() {
            @Override
            public void run() {
                FavouriteMovie obj = appDatabase.movieDao().getMovie(movieId);
                if (obj != null) {
                    favoriteCheckBox.setChecked(true);
                    favoriteCheckBox.setText("Remove from Favourites");
                } else {
                    favoriteCheckBox.setChecked(false);
                    favoriteCheckBox.setText("Add to Favourites");
                }
            }
        });


        favoriteCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    favouriteMovie = new FavouriteMovie();
                    favouriteMovie.setMovieId(movieObject.getMovieId());
                    favouriteMovie.setMovieTitle(movieObject.getMovieTitle());
                    favouriteMovie.setImagePath(IntentConstant.IMAGE_URL + IntentConstant.IMAGE_SIZE_185 + movieObject.getImagePath());
                    favouriteMovie.setOverview(movieObject.getOverview());
                    favouriteMovie.setRelease_date(movieObject.getRelease_date());
                    favouriteMovie.setVote_average(movieObject.getVote_average());
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            FavouriteMovie obj = appDatabase.movieDao().getMovie(movieId);
                            if (obj == null) {
                                appDatabase.movieDao().addMovieFavourite(favouriteMovie);
                            }

                        }
                    });
                    favoriteCheckBox.setText(R.string.remove_from_favourites);

                } else {
                    favouriteMovie = new FavouriteMovie();
                    favouriteMovie.setMovieId(movieObject.getMovieId());
                    mExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            appDatabase.movieDao().removeMovieFavourite(favouriteMovie);
                        }
                    });
                    favoriteCheckBox.setText(R.string.add_to_favourites);

                }
            }
        });


        trailerAdapter = new TrailerAdapter(this, trailerObjectArrayList, new TrailerAdapter.TrailerItemClickListener() {
            @Override
            public void onTrailerItemClick(String key) {
                String url = "https://www.youtube.com/watch?v=".concat(key);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        trailersRv.setAdapter(trailerAdapter);
        trailersRv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        DownloadTrailerContent downloadTrailerContent = new DownloadTrailerContent();
        downloadTrailerContent.execute(NetworkUtil.getTrailersListURL(movieId).toString());


        reviewsAdapter = new ReviewsAdapter(this, reviewsObjectArrayList, new ReviewsAdapter.ReviewsItemClickListener() {
            @Override
            public void onReviewClick(String url) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
        reviewsRv.setAdapter(reviewsAdapter);
        reviewsRv.setLayoutManager(new LinearLayoutManager(this));

        DownloadReviewsContent downloadReviewsContent = new DownloadReviewsContent();
        downloadReviewsContent.execute(NetworkUtil.getReviewsListURL(movieId).toString());

    }



    private class DownloadTrailerContent extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = NetworkUtil.getJSON(NetworkUtil.getTrailersListURL(movieId));
                return response;
            } catch (Exception e) {
                Toast.makeText(DetailActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (response != null) {
                NetworkUtil.loadTrailers(response);
                trailerAdapter.notifyDataSetChanged();

            } else {

                Toast.makeText(DetailActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class DownloadReviewsContent extends AsyncTask<String, Void, String> {
        String response;

        @Override
        protected String doInBackground(String... strings) {
            try {
                response = NetworkUtil.getJSON(NetworkUtil.getReviewsListURL(movieId));
                return response;
            } catch (Exception e) {
                Toast.makeText(DetailActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            if (response != null) {
                NetworkUtil.loadReviews(response);
                reviewsAdapter.notifyDataSetChanged();
                if (reviewsObjectArrayList.size() == 0) {
                    reviewNotAvailable.setVisibility(View.VISIBLE);
                }
                restoreScrollPosition();

            } else {
                Toast.makeText(DetailActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(mScrollPositionKey,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mScrollPosition  = savedInstanceState.getIntArray(mScrollPositionKey);

    }
    private void restoreScrollPosition(){
        if(mScrollPosition != null)
            mScrollView.post(new Runnable() {
                public void run() {
                    mScrollView.scrollTo(mScrollPosition[0], mScrollPosition[1]);
                }
            });
    }


}



