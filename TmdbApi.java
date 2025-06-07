package com.username.moviesapp.api; // Your actual package name

import com.username.moviesapp.models.Movie;
import com.username.moviesapp.models.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Path; // Make sure this is imported

public interface TmdbApi {

    @GET("movie/now_playing")
    Call<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("trending/{media_type}/{time_window}")
    Call<MoviesResponse> getTrendingMovies(
            @Path("media_type") String mediaType,
            @Path("time_window") String timeWindow,
            @Query("api_key") String apiKey
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetails(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    @GET("search/movie")
    Call<MoviesResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );
}