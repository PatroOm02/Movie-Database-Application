package com.username.moviesapp; // Your actual package name

import com.username.moviesapp.models.Movie;
import com.username.moviesapp.models.MoviesResponse;
import com.username.moviesapp.service.TmdbApiService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.Font;
import javafx.geometry.VPos;
import javafx.scene.control.ComboBox;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.ResourceBundle;
import java.util.Arrays;

public class MainController implements Initializable {

    @FXML
    private ListView<Movie> movieListView;
    @FXML
    private TextField searchField;
    @FXML
    private ProgressIndicator loadingIndicator;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private ComboBox<String> sortComboBox;

    private ObservableList<Movie> currentMovies = FXCollections.observableArrayList();
    private Stage primaryStage;

    private Image noPosterImage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        movieListView.setItems(currentMovies);

        // Creates a placeholder "No Poster Available" image
        noPosterImage = createNoPosterImage(92, 138);

        // Custom cell factory for the ListView to display movie details and posters
        movieListView.setCellFactory(param -> new ListCell<Movie>() {
            private ImageView imageView = new ImageView();
            private Label titleLabel = new Label();
            private Label overviewLabel = new Label();
            private VBox textContainer = new VBox(titleLabel, overviewLabel);
            private HBox cellLayout = new HBox(imageView, textContainer);

            {
                imageView.setFitWidth(92);
                imageView.setFitHeight(138);
                imageView.setPreserveRatio(true);
                cellLayout.setSpacing(10);
                textContainer.setSpacing(5);
                // Important: setPrefWidth(param.getWidth()) ensures the cell expands to fit the ListView
                setPrefWidth(param.getWidth());
                setGraphic(cellLayout);

                // These styles are specific to the ListCell and might not be fully controlled by external CSS
                // unless you define specific CSS classes for ListCell children.
                // For simplicity, keeping them here is fine, or define specific ListCell styles in style.css
                overviewLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #555555;");
                titleLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            }

            @Override
            protected void updateItem(Movie movie, boolean empty) {
                super.updateItem(movie, empty);
                if (empty || movie == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    titleLabel.setText(movie.getTitle());
                    String overview = movie.getOverview();
                    if (overview != null && overview.length() > 150) {
                        overviewLabel.setText(overview.substring(0, 150) + "...");
                    } else {
                        overviewLabel.setText(overview);
                    }

                    // Load poster image in a background thread
                    if (movie.getPosterPath() != null && !movie.getPosterPath().isEmpty()) {
                        String imageUrl = TmdbApiService.getBaseImageUrl("w92") + movie.getPosterPath();
                        new Thread(() -> {
                            try {
                                Image image = new Image(imageUrl, true); // true for background loading
                                Platform.runLater(() -> imageView.setImage(image)); // Update UI on FX Application Thread
                            } catch (Exception e) {
                                System.err.println("Error loading image for movie " + movie.getTitle() + ": " + e.getMessage());
                                Platform.runLater(() -> imageView.setImage(noPosterImage)); // Fallback on error
                            }
                        }).start();
                    } else {
                        imageView.setImage(noPosterImage); // Use placeholder if no poster path
                    }
                    setGraphic(cellLayout);
                }
            }
        });

        // Event handler for double-clicking a movie in the list
        movieListView.setOnMouseClicked(this::handleMovieSelection);

        // Populate ComboBox items programmatically
        sortComboBox.setItems(FXCollections.observableArrayList(
                "Popularity (Desc)",
                "Popularity (Asc)",
                "Release Date (Desc)",
                "Release Date (Asc)",
                "Title (A-Z)",
                "Title (Z-A)",
                "Rating (Desc)",
                "Rating (Asc)"
        ));
        // Set a default sort order
        sortComboBox.getSelectionModel().select("Popularity (Desc)");

        // Fetch initial set of movies when the controller initializes
        fetchNowPlayingMovies();
    }

    /**
     * Creates a simple placeholder image with "No Poster Available" text.
     * @param width Desired width of the placeholder.
     * @param height Desired height of the placeholder.
     * @return A JavaFX Image object.
     */
    private Image createNoPosterImage(double width, double height) {
        WritableImage img = new WritableImage((int) width, (int) height);
        Canvas canvas = new Canvas(width, height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(0, 0, width, height);
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(2);
        gc.strokeRect(0, 0, width, height);

        gc.setFill(Color.DARKGRAY);
        gc.setFont(Font.font("Arial", 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText("No Poster", width / 2, height / 2 - 10);
        gc.fillText("Available", width / 2, height / 2 + 10);

        return canvas.snapshot(null, img);
    }

    private void showLoading() {
        Platform.runLater(() -> {
            movieListView.setVisible(false);
            errorMessageLabel.setVisible(false);
            loadingIndicator.setVisible(true);
        });
    }

    private void hideLoading() {
        Platform.runLater(() -> {
            loadingIndicator.setVisible(false);
            movieListView.setVisible(true);
        });
    }

    private void showErrorMessage(String message) {
        Platform.runLater(() -> {
            currentMovies.clear(); // Clear existing movies on error
            movieListView.setVisible(false);
            loadingIndicator.setVisible(false);
            errorMessageLabel.setText(message);
            // The style is now controlled by style.css via styleClass="error-message-label"
            errorMessageLabel.setVisible(true);
        });
    }

    private void hideErrorMessage() {
        Platform.runLater(() -> {
            errorMessageLabel.setVisible(false);
            movieListView.setVisible(true);
        });
    }

    /**
     * Fetches "Now Playing" movies from the TMDB API.
     */
    private void fetchNowPlayingMovies() {
        showLoading();
        hideErrorMessage();

        new Thread(() -> {
            TmdbApiService.getTmdbApi().getNowPlayingMovies(TmdbApiService.API_KEY)
                    .enqueue(new Callback<MoviesResponse>() {
                        @Override
                        public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                            hideLoading();
                            if (response.isSuccessful() && response.body() != null) {
                                MoviesResponse moviesResponse = response.body();
                                System.out.println("Fetched " + moviesResponse.getResults().size() + " now playing movies.");
                                Platform.runLater(() -> {
                                    currentMovies.clear();
                                    currentMovies.addAll(moviesResponse.getResults());
                                    if (currentMovies.isEmpty()) {
                                        showErrorMessage("No 'Now Playing' movies found.");
                                    } else {
                                        hideErrorMessage();
                                    }
                                    applySorting(); // Apply sorting after fetching new data
                                });
                            } else {
                                String errorMsg = "Failed to fetch 'Now Playing' movies: " + response.code();
                                if (response.message() != null && !response.message().isEmpty()) {
                                    errorMsg += " - " + response.message();
                                }
                                System.err.println(errorMsg);
                                showErrorMessage(errorMsg);
                            }
                        }

                        @Override
                        public void onFailure(Call<MoviesResponse> call, Throwable t) {
                            hideLoading();
                            String errorMsg = "Network error fetching 'Now Playing' movies: " + t.getMessage();
                            System.err.println(errorMsg);
                            t.printStackTrace(); // Print full stack trace for debugging
                            showErrorMessage(errorMsg);
                        }
                    });
        }).start();
    }

    /**
     * Handles the search button action.
     * If the search field is empty, it refetches "Now Playing" movies.
     * Otherwise, it performs a movie search.
     */
    @FXML
    private void handleSearch() {
        String query = searchField.getText();
        if (query != null && !query.trim().isEmpty()) {
            System.out.println("Searching for: '" + query + "'");
            searchMovies(query.trim());
        } else {
            System.out.println("Search field is empty. Refetching now playing movies.");
            fetchNowPlayingMovies();
        }
    }

    /**
     * Searches for movies based on a query using the TMDB API.
     * @param query The search query string.
     */
    private void searchMovies(String query) {
        showLoading();
        hideErrorMessage();

        new Thread(() -> {
            TmdbApiService.getTmdbApi().searchMovies(TmdbApiService.API_KEY, query)
                    .enqueue(new Callback<MoviesResponse>() {
                        @Override
                        public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                            hideLoading();
                            if (response.isSuccessful() && response.body() != null) {
                                MoviesResponse moviesResponse = response.body();
                                System.out.println("Found " + moviesResponse.getResults().size() + " movies for query: '" + query + "'");
                                Platform.runLater(() -> {
                                    currentMovies.clear();
                                    currentMovies.addAll(moviesResponse.getResults());
                                    if (currentMovies.isEmpty()) {
                                        showErrorMessage("No movies found for '" + query + "'.");
                                    } else {
                                        hideErrorMessage();
                                    }
                                    applySorting(); // Apply sorting after fetching new data
                                });
                            } else {
                                String errorMsg = "Failed to search movies: " + response.code();
                                if (response.message() != null && !response.message().isEmpty()) {
                                    errorMsg += " - " + response.message();
                                }
                                System.err.println(errorMsg);
                                showErrorMessage(errorMsg);
                            }
                        }

                        @Override
                        public void onFailure(Call<MoviesResponse> call, Throwable t) {
                            hideLoading();
                            String errorMsg = "Network error searching movies: " + t.getMessage();
                            System.err.println(errorMsg);
                            t.printStackTrace(); // Print full stack trace for debugging
                            showErrorMessage(errorMsg);
                        }
                    });
        }).start();
    }

    /**
     * Handles the selection change in the sorting ComboBox.
     */
    @FXML
    private void handleSortSelection() {
        applySorting();
    }

    /**
     * Applies the selected sorting order to the current list of movies.
     */
    private void applySorting() {
        String selectedSort = sortComboBox.getSelectionModel().getSelectedItem();
        if (selectedSort == null || currentMovies.isEmpty()) {
            return; // No sort selected or no movies to sort
        }

        Comparator<Movie> comparator = null;

        switch (selectedSort) {
            case "Popularity (Desc)":
                comparator = Comparator.comparing(Movie::getPopularity, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "Popularity (Asc)":
                comparator = Comparator.comparing(Movie::getPopularity, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
            case "Release Date (Desc)":
                // Handle null or invalid dates by treating them as the minimum date for reverse order
                comparator = Comparator.comparing(movie -> {
                    try {
                        return movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty() ? LocalDate.parse(movie.getReleaseDate()) : LocalDate.MIN;
                    } catch (DateTimeParseException e) {
                        return LocalDate.MIN; // Return a default value for invalid dates
                    }
                }, Comparator.reverseOrder());
                break;
            case "Release Date (Asc)":
                // Handle null or invalid dates by treating them as the maximum date for natural order
                comparator = Comparator.comparing(movie -> {
                    try {
                        return movie.getReleaseDate() != null && !movie.getReleaseDate().isEmpty() ? LocalDate.parse(movie.getReleaseDate()) : LocalDate.MAX;
                    } catch (DateTimeParseException e) {
                        return LocalDate.MAX;
                    }
                });
                break;
            case "Title (A-Z)":
                comparator = Comparator.comparing(Movie::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
                break;
            case "Title (Z-A)":
                comparator = Comparator.comparing(Movie::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER.reversed()));
                break;
            case "Rating (Desc)":
                comparator = Comparator.comparing(Movie::getVoteAverage, Comparator.nullsLast(Comparator.reverseOrder()));
                break;
            case "Rating (Asc)":
                comparator = Comparator.comparing(Movie::getVoteAverage, Comparator.nullsLast(Comparator.naturalOrder()));
                break;
        }

        if (comparator != null) {
            FXCollections.sort(currentMovies, comparator);
        }
    }

    /**
     * Handles double-click events on the movie list to show movie details.
     * @param event The MouseEvent triggered.
     */
    private void handleMovieSelection(MouseEvent event) {
        if (event.getClickCount() == 2) { // Double-click
            Movie selectedMovie = movieListView.getSelectionModel().getSelectedItem();
            if (selectedMovie != null) {
                System.out.println("Selected Movie: " + selectedMovie.getTitle() + " (ID: " + selectedMovie.getId() + ")");
                showMovieDetail(selectedMovie);
            }
        }
    }

    /**
     * Loads and displays the movie detail view for the selected movie.
     * @param movie The Movie object to display details for.
     */
    private void showMovieDetail(Movie movie) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/movie-detail-view.fxml"));
            VBox movieDetailRoot = fxmlLoader.load();

            MovieDetailController detailController = fxmlLoader.getController();
            detailController.setStageAndMainScene(primaryStage, movieListView.getScene());
            detailController.setMovie(movie); // Pass the movie object to the detail controller

            Scene detailScene = new Scene(movieDetailRoot, 800, 600);
            primaryStage.setScene(detailScene);
            primaryStage.setTitle("Movie Details: " + movie.getTitle());
            primaryStage.show();

        } catch (IOException e) {
            System.err.println("Failed to load movie detail view: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> showErrorMessage("Failed to load movie details."));
        }
    }
}