/***
 * Copyright (c) 2016 Oscar Salguero www.oscarsalguero.com
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.oscarsalguero.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oscarsalguero.popularmovies.adapter.MovieAdapter;
import com.oscarsalguero.popularmovies.bean.MoviesResponse;
import com.oscarsalguero.popularmovies.model.Movie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * The Main Popular Movies app activity shows a grid of movies that can be sorted by most popular or
 * top rated
 * Created by RacZo on 11/19/16.
 */
public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private static final String LOG_TAG = MainActivity.class.getName();

    private CoordinatorLayout mCoordinatorLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<Movie> mMovies = new ArrayList<>();

    private static final String API_PATH_MOVIES_POPULAR = "popular";
    private static final String API_PATH_MOVIES_TOP_RATED = "top_rated";

    private String mSortOrder = API_PATH_MOVIES_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);

        mAdapter = new MovieAdapter(MainActivity.this, mMovies);
        mRecyclerView.setAdapter(mAdapter);
    }

    private AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.d(LOG_TAG, "Selected menu position: " + position);
            switch (position) {
                case 0:
                    setSortPath(API_PATH_MOVIES_POPULAR);
                    break;
                case 1:
                    setSortPath(API_PATH_MOVIES_TOP_RATED);
                    break;
                default:
                    setSortPath(API_PATH_MOVIES_POPULAR);
                    break;
            }
            getMovies();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Log.d(LOG_TAG, "Nothing selected");
        }

    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        try {
            Context supportActionBarThemedContext = getSupportActionBar().getThemedContext();
            MenuItem menuItem = menu.findItem(R.id.action_sort);
            SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(
                    supportActionBarThemedContext,
                    R.array.sort_options,
                    R.layout.spinner_dropdown_item);
            AppCompatSpinner sortOptionsSpinner = new AppCompatSpinner(supportActionBarThemedContext);
            sortOptionsSpinner.setAdapter(spinnerAdapter);
            sortOptionsSpinner.setOnItemSelectedListener(onItemSelectedListener);
            MenuItemCompat.setActionView(menuItem, sortOptionsSpinner);
        } catch (Exception e) {
            Log.d(LOG_TAG, "An error has occurred creating sort order dropdown menu in action bar", e);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Gets the sort order
     * @return a {@link String} with the sort order
     */
    public String getSortOrder() {
        return mSortOrder;
    }

    /**
     * Sets the sort order
     * @param sortOrder a {@link String} with the sort order
     */
    public void setSortPath(String sortOrder) {
        this.mSortOrder = sortOrder;
    }

    /**
     * Gets the movies
     */
    private void getMovies() {
        new FetchMoviesTask().execute(getSortOrder());
    }

    @Override
    public void onRefresh() {
        Log.d(LOG_TAG, "Will refresh...");
        getMovies();
    }

    /**
     * Async task to fetch movie data from API
     */
    private class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String apiPathSortOrder = params[0];
            String apiKey = BuildConfig.THE_MOVIE_DB_API_KEY;
            if (TextUtils.isEmpty(apiKey)) {
                Snackbar.make(mCoordinatorLayout, "API Key Not Defined. Please add it to your ~/.gradle/gradle.properties files as 'TheMovieDBAPIKey'.", Snackbar.LENGTH_SHORT).show();
            }
            try {

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme(Constants.API_SCHEME)
                        .authority(Constants.API_BASE_URL)
                        .appendPath(Constants.API_PATH_VERSION)
                        .appendPath(Constants.API_PATH_MOVIE)
                        .appendPath(apiPathSortOrder)
                        .appendQueryParameter(Constants.API_PARAM_API_KEY, apiKey);

                Log.d(LOG_TAG, "Hitting URL: " + uriBuilder.build().toString());

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String jsonResponseString = null;

                try {

                    URL url = new URL(uriBuilder.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod(Constants.METHOD_GET);
                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        return null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        return null;
                    }
                    jsonResponseString = buffer.toString();

                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error", e);
                    Snackbar.make(mCoordinatorLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    return null;
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error", e);
                    Snackbar.make(mCoordinatorLayout, e.getMessage(), Snackbar.LENGTH_SHORT).show();
                    return null;
                } finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                        }
                    }
                }

                return jsonResponseString;

            } catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String jsonResponseString) {

            Log.v(LOG_TAG, "JSON response: " + jsonResponseString);

            if (jsonResponseString != null) {
                try {
                    Gson gson = new GsonBuilder().create();
                    // Using third party library Gson to parse movies from JSON to a Java object
                    MoviesResponse moviesResponse = gson.fromJson(jsonResponseString, MoviesResponse.class);
                    if (moviesResponse.getResults() != null && moviesResponse.getResults().size() > 0) {
                        mMovies.clear();
                        mMovies.addAll(moviesResponse.getResults());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Log.e(LOG_TAG, "Movie results is null");
                        Snackbar.make(mCoordinatorLayout, getString(R.string.error_no_results), Snackbar.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    String errorMessage = "Error parsing JSON response";
                    Log.e(LOG_TAG, errorMessage, e);
                    Snackbar.make(mCoordinatorLayout, errorMessage, Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(mCoordinatorLayout, getString(R.string.error_loading_movies), Snackbar.LENGTH_SHORT).show();
            }

            // Stopping swipe refresh layout
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        Log.d(LOG_TAG, "Stopped refreshing");
                    }
                });
            }
        }

    }

}
