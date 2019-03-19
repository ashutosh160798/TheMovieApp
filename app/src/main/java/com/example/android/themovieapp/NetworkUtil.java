package com.example.android.themovieapp;

import android.net.Uri;
import android.util.Log;

import com.example.android.themovieapp.Database.FavouriteMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by ashu on 27-06-2018.
 */

class NetworkUtil {

    public static String getJSON(Uri builtUri) {
        InputStream inputStream;
        StringBuffer buffer;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String moviesJson;

        try {
            URL url = new URL(builtUri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();


            inputStream = urlConnection.getInputStream();
            buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                return null;
            }
            moviesJson = buffer.toString();
        } catch (IOException e) {
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException ignored) {

                }
            }
        }

        return moviesJson;
    }

    public static ArrayList<String> loadMovieList(String jsonString) {

        ArrayList<String> img = new ArrayList<>();

        try {
            if (jsonString != null) {
                JSONObject moviesObject = new JSONObject(jsonString);
                JSONArray moviesArray = moviesObject.getJSONArray("results");


                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movie = moviesArray.getJSONObject(i);
                    FavouriteMovie movieObjectItem = new FavouriteMovie();
                    movieObjectItem.setMovieTitle(movie.getString("title"));
                    movieObjectItem.setMovieId(movie.getInt("id"));
                    if (movie.getString("overview").equals("null")) {
                        movieObjectItem.setOverview("No Overview was Found");
                    } else {
                        movieObjectItem.setOverview(movie.getString("overview"));
                    }
                    if (movie.getString("release_date").equals("null")) {
                        movieObjectItem.setRelease_date("Unknown Release Date");
                    } else {
                        movieObjectItem.setRelease_date(movie.getString("release_date"));
                    }
                    movieObjectItem.setVote_average(movie.getString("vote_average"));
                    movieObjectItem.setImagePath(movie.getString("poster_path"));
                    if (movie.getString("poster_path").equals("null")) {
                        img.add(IntentConstant.IMAGE_NOT_FOUND);
                    } else {
                        img.add(IntentConstant.IMAGE_URL + IntentConstant.IMAGE_SIZE_185 + movie.getString("poster_path"));

                    }
                    MainActivity.movieObjectList.add(movieObjectItem);
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return img;
    }


    public static void loadTrailers(String jsonString) {


        try {
            if (jsonString != null) {
                JSONObject trailersObject = new JSONObject(jsonString);
                JSONArray trailersArray = trailersObject.getJSONArray("results");


                for (int i = 0; i < trailersArray.length(); i++) {
                    JSONObject trailer = trailersArray.getJSONObject(i);
                    TrailerObject trailerObject = new TrailerObject();
                    trailerObject.setId(trailer.getString("id"));
                    trailerObject.setIso_639_1(trailer.getString("iso_639_1"));
                    trailerObject.setIso_3166_1(trailer.getString("iso_3166_1"));
                    trailerObject.setKey(trailer.getString("key"));
                    trailerObject.setName(trailer.getString("name"));
                    trailerObject.setSite(trailer.getString("site"));
                    trailerObject.setSize(trailer.getInt("size"));
                    trailerObject.setType(trailer.getString("type"));
                    DetailActivity.trailerObjectArrayList.add(trailerObject);


                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static void loadReviews(String jsonString) {


        try {
            if (jsonString != null) {
                JSONObject reviewsJsonObject = new JSONObject(jsonString);
                JSONArray reviewsArray = reviewsJsonObject.getJSONArray("results");


                for (int i = 0; i < reviewsArray.length(); i++) {
                    JSONObject review = reviewsArray.getJSONObject(i);
                    ReviewsObject reviewObject = new ReviewsObject();
                    reviewObject.setId(review.getString("id"));
                    reviewObject.setAuthor(review.getString("author"));
                    reviewObject.setContent(review.getString("content"));
                    reviewObject.setUrl(review.getString("url"));

                    DetailActivity.reviewsObjectArrayList.add(reviewObject);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static Uri getMoviesListURL(String sortType) {
        Uri builtUri = Uri.parse(IntentConstant.BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(sortType)
                .appendQueryParameter(IntentConstant.API_KEY_PARAM, IntentConstant.API_KEY_VALUE)
                .appendQueryParameter(IntentConstant.ORIGINAL_LANGUAGE, "en-US")
                .build();

        return builtUri;
    }

    public static Uri getTrailersListURL(int movieId) {
        Uri builtUri = Uri.parse(IntentConstant.BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath("videos")
                .appendQueryParameter(IntentConstant.API_KEY_PARAM, IntentConstant.API_KEY_VALUE)
                .appendQueryParameter(IntentConstant.ORIGINAL_LANGUAGE, "en-US")
                .build();

        Log.d("URL", builtUri.toString());
        return builtUri;

    }

    public static Uri getReviewsListURL(int movieId) {
        Uri builtUri = Uri.parse(IntentConstant.BASE_URL).buildUpon()
                .appendPath("movie")
                .appendPath(Integer.toString(movieId))
                .appendPath("reviews")
                .appendQueryParameter(IntentConstant.API_KEY_PARAM, IntentConstant.API_KEY_VALUE)
                .appendQueryParameter(IntentConstant.ORIGINAL_LANGUAGE, "en-US")
                .build();

        return builtUri;

    }

}
