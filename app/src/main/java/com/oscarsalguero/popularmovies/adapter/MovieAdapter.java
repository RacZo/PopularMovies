package com.oscarsalguero.popularmovies.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.oscarsalguero.popularmovies.BuildConfig;
import com.oscarsalguero.popularmovies.R;
import com.oscarsalguero.popularmovies.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Movies adapter
 * Created by RacZo on 11/19/16.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final String LOG_TAG = MovieAdapter.class.getName();

    private Context mContext;

    final List<Movie> mItems;

    private static final String IMAGES_URL = BuildConfig.IMAGES_BASE_URL + BuildConfig.IMAGES_PATH_W185;

    public MovieAdapter(Context context, List<Movie> list) {
        this.mContext = context;
        this.mItems = list;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        final View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieViewHolder movieViewHolder, final int position) {
        movieViewHolder.currentItem = mItems.get(position);
        if (movieViewHolder.currentItem.getPosterPath() != null) {
            Picasso.with(mContext)
                    .load(IMAGES_URL + movieViewHolder.currentItem.getPosterPath())
                    .into(movieViewHolder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final Context context;
        public Movie currentItem;
        public ImageView imageView;

        public MovieViewHolder(View view) {
            super(view);
            context = view.getContext();
            imageView = (ImageView) itemView.findViewById(R.id.image_view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Log.d(LOG_TAG, "Movie clicked!");
        }

        public Context getContext() {
            return context;
        }

    }

}
