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

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.oscarsalguero.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Movie Detail activity to show movie poster, title, release date, average vote and overview
 * Created by RacZo on 11/19/16.
 */
public class MovieDetailActivity extends AppCompatActivity {

    public static final String LOG_TAG = MovieDetailActivity.class.getName();

    public static final String PARAM_MOVIE = "param_movie";

    private ImageView mImageViewPoster;
    private TextView mTextViewTitle;
    private TextView mTextViewReleaseDate;
    private TextView mTextViewVote;
    private TextView mTextViewOverview;

    private Movie mMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mImageViewPoster = (ImageView) findViewById(R.id.image_view_poster);
        mTextViewTitle = (TextView) findViewById(R.id.text_view_title);
        mTextViewReleaseDate = (TextView) findViewById(R.id.text_view_release_date);
        mTextViewVote = (TextView) findViewById(R.id.text_view_vote_average);
        mTextViewOverview = (TextView) findViewById(R.id.text_view_overview);

        Intent intent = getIntent();

        if (intent != null) {
            if (intent.hasExtra(PARAM_MOVIE)) {
                mMovie = intent.getParcelableExtra(PARAM_MOVIE);
                if (mMovie != null) {
                    refreshUI(mMovie);
                }
            }
        }

    }

    private void refreshUI(Movie movie) {
        if (movie.getPosterPath() != null) {
            Picasso.with(this)
                    .load(Constants.IMAGES_URL + movie.getPosterPath())
                    .into(mImageViewPoster);
        }
        mTextViewTitle.setText(movie.getTitle());
        SimpleDateFormat releaseDateFormat = new SimpleDateFormat("YYYY-MM-DD", Locale.US);
        SimpleDateFormat releaseDateDisplatFormat = new SimpleDateFormat("YYYY", Locale.US);
        Date releaseDate = null;
        try {
            releaseDate = releaseDateFormat.parse(movie.getReleaseDate());
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Could not parse release date", e);
        }
        mTextViewReleaseDate.setText(releaseDateDisplatFormat.format(releaseDate));
        mTextViewVote.setText(getString(R.string.label_vote, String.valueOf(Math.round(movie.getVoteAverage())), String.valueOf(movie.getVoteCount())));
        mTextViewOverview.setText(movie.getOverview());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
