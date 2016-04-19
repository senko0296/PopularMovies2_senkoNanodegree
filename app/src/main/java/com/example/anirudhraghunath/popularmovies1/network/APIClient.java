package com.example.anirudhraghunath.popularmovies1.network;

import com.example.anirudhraghunath.popularmovies1.MovieVideos;
import com.example.anirudhraghunath.popularmovies1.resources.Constants;
import com.example.anirudhraghunath.popularmovies1.utilities.MovieReviews;
import com.example.anirudhraghunath.popularmovies1.utilities.Movies;



import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.GET;
import retrofit.http.Path;

/**
 * Created by anirudhraghunath on 31/01/16.
 */
public class APIClient {

    public static tmdbInterface movietmdbInterface = null;

    public static tmdbInterface getApi(){

        if(movietmdbInterface == null){

            RestAdapter adapter = new RestAdapter.Builder()
                    .setEndpoint(Constants.URL_BASE)
                    .build();
            movietmdbInterface = adapter.create(tmdbInterface.class);
        }
        return movietmdbInterface;
    }

    public interface tmdbInterface {

        @GET("/movie/popular?api_key=" + Constants.API_KEY)
        public void getPopular(Callback<Movies> moviesCallback);

        @GET("/movie/top_rated?api_key=" + Constants.API_KEY)
        public void getTopRated(Callback<Movies> moviesCallback);

        @GET("/movie/{id}/videos?api_key=" + Constants.API_KEY)
        void getMovieVideosFromId(@Path("id") String id, Callback<MovieVideos> movieVideosCallback);

        @GET("/movie/{id}/reviews?api_key=" + Constants.API_KEY)
        void getMovieReviewsFromId(@Path("id") String id, Callback<MovieReviews> movieReviewsCallback);
    }
}
