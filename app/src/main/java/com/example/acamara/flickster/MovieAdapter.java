package com.example.acamara.flickster;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.acamara.flickster.models.Config;
import com.example.acamara.flickster.models.Movie;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

import static com.example.acamara.flickster.MovieListActivity.API_KEY_PARAM;

/**
 * Created by acamara on 6/21/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder>
{
    // list of movies
    ArrayList<Movie> movies;
    // config needed for image urls
    Config config;
    //contect for rendering
    Context context;

    // initialized with list

    public MovieAdapter(ArrayList<Movie> movies)
    {
        this.movies = movies;
    }

    public void setConfig(Config config)
    {
        this.config = config;
    }

    //creates and inflates a new view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        //get the context and create the inflator
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        //create the view using the item_movie layout
        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        //return a new ViewHolder
        return new ViewHolder(movieView);
    }

    //binds an inflated view to a new item
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        // get the movie data at the specified position
        final Movie movie = movies.get(position);
        //populate the view with the movie data
        holder.tvTitle.setText(movie.getTitle());
        holder.tvOverview.setText(movie.getOverview());

        //determine the current orientation
        boolean isPortrait = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        // build url for poster image
        String imageUrl = null;

        //if in portrait mode, load the poster image
        if (isPortrait)
        {
            imageUrl = config.getImageUrl(config.getPosterSize(), movie.getPosterPath());
        }
        else
        {
            //load the backdrop image
            imageUrl = config.getImageUrl(config.getBackdropSize(), movie.getBackdropPath());
            holder.ibPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsyncHttpClient client = new AsyncHttpClient();
                    // set the request parameters
                    RequestParams params = new RequestParams();
                    params.put(API_KEY_PARAM, context.getString(R.string.api_key)); // API key, always required

                    client.get("https://api.themoviedb.org/3/movie/"+movie.getId()+"/videos", params, new JsonHttpResponseHandler()
                    {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response)
                        {
                            // load the results into movies list
                            try
                            {
                                String youtubeKey ="";
                                JSONArray results = response.getJSONArray("results");
                                if (results.length() > 0 && results.getJSONObject(0).getString("site").equals("YouTube"))
                                {
                                    youtubeKey = results.getJSONObject(0).getString("key");
                                    Log.i("adapter", youtubeKey);
                                }
                                Intent intent = new Intent(context, MovieTrailerActivity.class);
                                intent.putExtra("youTubeKey", youtubeKey);
                                context.startActivity(intent);

                            }
                            catch (JSONException e)
                            {
                                Log.i("adapter", "error");
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable)
                        {
                            Log.i("adapter", "error");
                        }
                    });
                }
            });
        }

        // build url for the trailer
        String trailerUrl = movie.getTrailerUrl();

        //get correct placeholder and imageview for the current orientation
        int placeholderId = isPortrait ? R.drawable.flicks_movie_placeholder : R.drawable.flicks_backdrop_placeholder;
        ImageView imageView = isPortrait ? holder.ivPosterImage : holder.ivBackdropImage;

        //load image using glide
        Glide.with(context)
                .load(imageUrl)
                .bitmapTransform(new RoundedCornersTransformation(context, 50,0))
                .placeholder(placeholderId)
                .error(placeholderId)
                .into(imageView);
    }

    // returns the total number of items in the list
    @Override
    public int getItemCount()
    {
        return movies.size();
    }

    // create the viewholder as a static inner class
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        // track view objects
        ImageView ivPosterImage;
        ImageView ivBackdropImage;
        TextView tvTitle;
        TextView tvOverview;
        ImageButton ibPlayButton;
        ImageView ivDetails;

        public ViewHolder(View itemView)
        {
            super(itemView);
            // lookup view objects by id
            ivPosterImage = (ImageView) itemView.findViewById(R.id.ivPosterImage);
            ivBackdropImage = (ImageView) itemView.findViewById(R.id.ivBackdropImage);
            ivDetails = (ImageView) itemView.findViewById(R.id.ivDetails);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            ibPlayButton = (ImageButton) itemView.findViewById(R.id.ibPlayButton);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // gets the item position
            int position = getAdapterPosition();
            // make sure the position is valid (actually exists)
            if (position != RecyclerView.NO_POSITION)
            {
                // get the movie position, wont work if class is static
                Movie movie = movies.get(position);
                //create intent for the new activity
                Intent intent = new Intent(context, MovieDetailsActivity.class);
                //serialize the movie using parceler, use its short name as a key
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                //show activity
                context.startActivity(intent);
            }
        }
    }
}
