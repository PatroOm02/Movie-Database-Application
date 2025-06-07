package com.username.moviesapp.models; // Ensure this matches your actual package path

import java.util.List; // For the 'results' field which is a list of Movie objects
import com.google.gson.annotations.SerializedName; // For @SerializedName annotation

public class MoviesResponse {

    // --- Fields ---
    // These fields correspond to the top-level keys in the JSON response
    // for API calls that return a list of movies (like /trending or /now_playing).

    private int page; // Corresponds to "page" in JSON (current page number)

    // This is crucial: the "results" key in JSON contains an array of movie objects.
    // GSON will map each object in that array to your 'Movie' class.
    private List<Movie> results; // Corresponds to "results" in JSON (a list of Movie objects)

    // Again, @SerializedName is used because JSON keys have underscores.
    @SerializedName("total_pages")
    private int totalPages; // Corresponds to "total_pages" in JSON (total number of pages)

    @SerializedName("total_results")
    private int totalResults; // Corresponds to "total_results" in JSON (total number of movies available)


    // --- Getters ---
    // Provide public methods to access the data.

    public int getPage() {
        return page;
    }

    public List<Movie> getResults() {
        return results;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }

    // No constructor needed here for GSON to work for deserialization.
    // Setters are typically not needed if you're just reading API responses.
}