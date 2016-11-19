package com.oscarsalguero.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Movie
 * Created by RacZo on 11/17/16.
 */

public class Movie implements Parcelable {

    @SerializedName("poster_path")
    private String posterPath;

    private boolean adult;

    private String overview;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("original_title")
    private String originalTitle;

    private long id;

    @SerializedName("original_language")
    private String originalLanguage;

    private String title;

    @SerializedName("backdrop_path")
    private String backdropPath;

    private float popularity;

    @SerializedName("vote_count")
    private int voteCount;

    private boolean video;

    @SerializedName("vote_average")
    private float voteAverage;

    Movie(Parcel in) {
        this.posterPath = in.readString();
        //this.adult = ; // TODO: Deal with booleans
        this.overview = in.readString();
        this.releaseDate = in.readString();
        this.originalTitle = in.readString();
        this.id = in.readLong();
        this.originalLanguage = in.readString();
        this.title = in.readString();
        this.backdropPath = in.readString();
        this.popularity = in.readFloat();
        this.voteCount = in.readInt();
        //this.video = ;
        this.voteAverage = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(posterPath);
        //parcel.write // TODO: Write booleans
        parcel.writeString(overview);
        parcel.writeString(releaseDate);
        parcel.writeString(originalTitle);
        parcel.writeLong(id);
        parcel.writeString(originalLanguage);
        parcel.writeString(title);
        parcel.writeString(backdropPath);
        parcel.writeFloat(popularity);
        parcel.writeInt(voteCount);
        //parcel.write
        parcel.writeFloat(voteAverage);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {

        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }

    };


}
