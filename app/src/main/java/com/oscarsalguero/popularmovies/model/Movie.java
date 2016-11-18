package com.oscarsalguero.popularmovies.model;

import com.google.gson.annotations.SerializedName;

/**
 * Movie
 * Created by RacZo on 11/17/16.
 */

public class Movie { // implements Parcelable {

    private long id;

    @SerializedName("poster_path")
    private String posterPath;

    private boolean adult;

    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("original_title")
    private String originalTitle;

    /*

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }

    */
}
