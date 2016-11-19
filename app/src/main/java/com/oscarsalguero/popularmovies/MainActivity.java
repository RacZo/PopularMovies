package com.oscarsalguero.popularmovies;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.oscarsalguero.popularmovies.model.Movie;
import com.oscarsalguero.popularmovies.model.MoviesResponse;

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

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private MovieAdapter mAdapter;
    private GridLayoutManager mGridLayoutManager;
    private List<Movie> mMovies = new ArrayList<>();

    private static final String API_SCHEME = "https";
    private static final String API_BASE_URL = "api.themoviedb.org";
    private static final String API_PATH_VERSION = "3";
    private static final String API_PATH_MOVIE = "movie";
    private static final String API_PATH_MOVIES_POPULAR = "popular";
    private static final String API_PATH_MOVIES_TOP_RATED = "top_rated";
    private static final String API_PARAM_API_KEY = "api_key";

    private String mSortOrder = API_PATH_MOVIES_POPULAR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        mAdapter = new MovieAdapter(MainActivity.this, mMovies);
        mRecyclerView.setAdapter(mAdapter);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setOrientation(GridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //getMovies(getSortOrder());
    }

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
            AppCompatSpinner reportTypeSpinner = new AppCompatSpinner(supportActionBarThemedContext);
            reportTypeSpinner.setAdapter(spinnerAdapter);
            reportTypeSpinner.setOnItemSelectedListener(onItemSelectedListener);
            MenuItemCompat.setActionView(menuItem, reportTypeSpinner);
        } catch (Exception e) {
            Log.d(LOG_TAG, "An error has occurred creating sort order dropdown menu in action bar", e);
        }
        return true;
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

    public String getSortOrder() {
        return mSortOrder;
    }

    public void setSortPath(String sortOrder) {
        this.mSortOrder = sortOrder;
    }

    private void getMovies() {
        new FetchMoviesTask().execute(getSortOrder());
    }

    @Override
    public void onRefresh() {
        getMovies();
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, String> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected String doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            String apiPathSortOrder = params[0];

            try {

                Uri.Builder uriBuilder = new Uri.Builder();
                uriBuilder.scheme(API_SCHEME)
                        .authority(API_BASE_URL)
                        .appendPath(API_PATH_VERSION)
                        .appendPath(API_PATH_MOVIE)
                        .appendPath(apiPathSortOrder)
                        .appendQueryParameter(API_PARAM_API_KEY, BuildConfig.THE_MOVIE_DB_API_KEY);

                Log.v(LOG_TAG, "Hitting URL: " + uriBuilder.build().toString());

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String jsonResponseString = null;

                try {

                    URL url = new URL(uriBuilder.toString());
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
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
                    Log.e(LOG_TAG, "Error ", e);
                    return null;
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error ", e);
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

            Log.d(LOG_TAG, "JSON response: " + jsonResponseString);

            if (jsonResponseString != null) {

                try {
                    Gson gson = new GsonBuilder().create();
                    MoviesResponse moviesResponse = gson.fromJson(jsonResponseString, MoviesResponse.class);
                    if (moviesResponse.getResults() != null) {

                        mMovies.clear();
                        mMovies.addAll(moviesResponse.getResults());

                        mAdapter.notifyDataSetChanged();

                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Error parsing response", e);
                }

            }

        }
    }

}
