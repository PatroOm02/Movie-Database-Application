package com.username.moviesapp; // Your actual package name

import com.username.moviesapp.models.Movie;
import com.username.moviesapp.service.TmdbApiService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.text.DecimalFormat;
import java.util.stream.Collectors;

public class MovieDetailController {

    @FXML
    private Label movieTitleLabel;
    @FXML
    private ImageView posterImageView;
    @FXML
    private Label taglineLabel;
    @FXML
    private Label ratingLabel;
    @FXML
    private Label releaseDateLabel;
    @FXML
    private Label runtimeLabel;
    @FXML
    private TextFlow genresTextFlow;
    @FXML
    private Label overviewLabel;

    private Stage primaryStage;
    private Scene mainScene; // To store the reference to the main application scene
    private Movie currentMovie; // To store the movie being displayed

    // This method is called by MainApplication to pass the stage and the main scene
    public void setStageAndMainScene(Stage stage, Scene mainScene) {
        this.primaryStage = stage;
        this.mainScene = mainScene;
    }

    // This method is called by MainController to pass the selected movie
    public void setMovie(Movie movie) {
        this.currentMovie = movie;
        displayMovie(movie);
        fetchMovieDetails(movie.getId()); // Fetch additional details for the movie
    }

    private void displayMovie(Movie movie) {
        // Display basic details already available from the list view
        movieTitleLabel.setText(movie.getTitle());
        overviewLabel.setText(movie.getOverview());

        // Load poster image
        if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
            String imageUrl = TmdbApiService.getBaseImageUrl("w500") + movie.getPosterPath(); // Use a larger size for detail view
            new Thread(() -> {
                try {
                    Image image = new Image(imageUrl, true);
                    Platform.runLater(() -> posterImageView.setImage(image));
                } catch (Exception e) {
                    System.err.println("Error loading detail poster for movie " + movie.getTitle() + ": " + e.getMessage());
                    // Fallback to a placeholder if needed, though not explicitly created here.
                }
            }).start();
        } else {
            // You might want to create a "no poster" placeholder for the detail view too
            posterImageView.setImage(null); // Clear image if no poster
        }

        // Display existing basic details if available, otherwise they'll be updated by fetchMovieDetails
        DecimalFormat df = new DecimalFormat("#.#");
        ratingLabel.setText(movie.getVoteAverage() != null ? df.format(movie.getVoteAverage()) + "/10" : "N/A");
        releaseDateLabel.setText(movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty() ? movie.getReleaseDate() : "N/A");
    }

    // Fetches additional movie details (like tagline, runtime, full genres) using the movie ID
    private void fetchMovieDetails(int movieId) {
        TmdbApiService.getTmdbApi().getMovieDetail(movieId, TmdbApiService.API_KEY)
                .enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Movie fullMovie = response.body();
                            Platform.runLater(() -> {
                                // Update UI with more detailed information
                                if (fullMovie.getOverview() != null && !fullMovie.getOverview().isEmpty()) {
                                    overviewLabel.setText(fullMovie.getOverview()); // Ensure full overview is shown
                                } else {
                                    overviewLabel.setText("No overview available.");
                                }

                                taglineLabel.setText(fullMovie.getTagline() != null && !fullMovie.getTagline().isEmpty() ? fullMovie.getTagline() : "");
                                runtimeLabel.setText(fullMovie.getRuntime() != null ? fullMovie.getRuntime() + " min" : "N/A");

                                if (fullMovie.getGenres() != null && !fullMovie.getGenres().isEmpty()) {
                                    String genreNames = fullMovie.getGenres().stream()
                                            .map(Movie.Genre::getName)
                                            .collect(Collectors.joining(", "));
                                    genresTextFlow.getChildren().setAll(new Text(genreNames));
                                } else {
                                    genresTextFlow.getChildren().setAll(new Text("N/A"));
                                }
                            });
                        } else {
                            System.err.println("Failed to fetch full movie details: " + response.code() + " - " + response.message());
                            Platform.runLater(() -> {
                                // Display a message if full details couldn't be loaded
                                taglineLabel.setText("Could not load full details.");
                                runtimeLabel.setText("N/A");
                                genresTextFlow.getChildren().setAll(new Text("N/A"));
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        System.err.println("Network error fetching full movie details: " + t.getMessage());
                        t.printStackTrace();
                        Platform.runLater(() -> {
                            // Display a message on network failure
                            taglineLabel.setText("Network error loading details.");
                            runtimeLabel.setText("N/A");
                            genresTextFlow.getChildren().setAll(new Text("N/A"));
                        });
                    }
                });
    }

    /**
     * Handles the action for the "Back to List" button.
     * Switches the primary stage's scene back to the main movie list.
     */
    @FXML
    private void handleBackToList() {
        if (primaryStage != null && mainScene != null) {
            primaryStage.setScene(mainScene);
            primaryStage.setTitle("Movie Database App"); // Reset title
        }
    }
}