package com.username.moviesapp.service;

import com.username.moviesapp.models.Movie;
import com.username.moviesapp.models.MoviesResponse;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {

    String BASE_URL = "https://api.themoviedb.org/3/";
    String API_KEY = "bbee45b8609acd7decb8b0047174f64b"; // <<< IMPORTANT: REPLACE WITH YOUR ACTUAL TMDB API KEY
    String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/";

    @GET("movie/now_playing")
    Call<MoviesResponse> getNowPlayingMovies(@Query("api_key") String apiKey);

    @GET("search/movie")
    Call<MoviesResponse> searchMovies(
            @Query("api_key") String apiKey,
            @Query("query") String query
    );

    @GET("movie/{movie_id}")
    Call<Movie> getMovieDetail(
            @Path("movie_id") int movieId,
            @Query("api_key") String apiKey
    );

    static TmdbApiService getTmdbApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit.create(TmdbApiService.class);
    }

    static String getBaseImageUrl(String size) {
        return IMAGE_BASE_URL + size;
    }
}