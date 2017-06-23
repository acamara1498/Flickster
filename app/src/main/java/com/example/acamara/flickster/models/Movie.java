package com.example.acamara.flickster.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

/**
 * Created by acamara on 6/21/17.
 */

@Parcel
public class Movie
{
    // values from API
    long id;
    double voteAverage;
    String title;
    String overview;
    String posterPath;// only the path
    String backdropPath;
    String trailerPath;

    //empty constructor for the parceler library
    public Movie() {}

    // initialize from JSON data
    public Movie(JSONObject movie) throws JSONException
    {
        id = movie.getLong("id");
        title = movie.getString("title");
        overview = movie.getString("overview");
        posterPath = movie.getString("poster_path");
        //trailerPath = object.getString("");
        backdropPath = movie.getString("backdrop_path");
        voteAverage = movie.getDouble("vote_average");
    }

    public double getVoteAverage() {
        return voteAverage;
    }

    public String getTitle()
    {
        return title;
    }

    public String getOverview()
    {
        return overview;
    }

    public String getPosterPath()
    {
        return posterPath;
    }

    public String getBackdropPath()
    {
        return backdropPath;
    }

    public long getId() {
        return id;
    }


    public String getTrailerUrl() {
        return trailerPath;
    }


}
