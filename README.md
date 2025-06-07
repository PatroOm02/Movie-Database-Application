# Movie Database App

A simple JavaFX application to browse "Now Playing" movies, search for movies, sort them, and view detailed information using The Movie Database (TMDB) API.

## ✨ Features

* **Browse Now Playing:** See a list of currently playing movies.
* **Search Movies:** Search for movies by title.
* **Sort Options:** Sort movie lists by:
    * Popularity (Ascending/Descending)
    * Release Date (Ascending/Descending)
    * Title (A-Z / Z-A)
    * Rating (Ascending/Descending)
* **Movie Details:** Double-click any movie in the list to view its detailed information, including:
    * Title and Tagline
    * Poster
    * Overview
    * Average Rating
    * Release Date
    * Runtime
    * Genres
* **Responsive UI:** Basic styling using CSS for a clean look.
* **Error Handling:** Displays messages for network issues or no search results.

## 🚀 Technologies Used

* **Java 17+**
* **JavaFX:** For the graphical user interface.
* **Maven:** For project management and build automation.
* **Retrofit 2:** A type-safe HTTP client for Android and Java, used to interact with the TMDB API.
* **Gson:** A Java serialization/deserialization library to convert Java Objects into JSON and vice-versa.

## 🔑 Prerequisites

Before running this application, ensure you have the following installed:

* **Java Development Kit (JDK) 17 or higher** (e.g., from OpenJDK, Adoptium, Oracle).
* **Apache Maven 3.6+**
* **An Integrated Development Environment (IDE):** IntelliJ IDEA Community Edition or Ultimate is highly recommended as the instructions below are tailored for it.
* **TMDB API Key:**
    1.  Go to [The Movie Database (TMDB)](https://www.themoviedb.org/) and create a free account.
    2.  Navigate to your account settings and then to the "API" section.
    3.  Request a new API key (Developer/v3 API).
    4.  Keep this API key handy, as you will need to insert it into the application code.

## ⚙️ Setup and Installation

1.  **Clone the Repository (or Download):**
    If you're using Git:
    ```bash
    git clone <repository_url> # Replace with your repository URL if applicable
    cd MovieDatabaseApp
    ```
    If you downloaded a ZIP:
    Unzip the project to your desired location.

2.  **Open in IntelliJ IDEA:**
    * Open IntelliJ IDEA.
    * Select `File` > `Open` and navigate to the `MovieDatabaseApp` project folder.
    * IntelliJ IDEA should automatically detect the `pom.xml` file and configure the project as a Maven project.

3.  **Maven Re-import:**
    * After opening the project, it's a good practice to force a Maven re-import.
    * In IntelliJ IDEA, open the **Maven Tool Window** (usually on the right side).
    * Click the **"Reimport All Maven Projects"** icon (a circular arrow).

4.  **Configure TMDB API Key:**
    * Navigate to `src/main/java/com/username/moviesapp/service/TmdbApiService.java` (replace `com.username.moviesapp` with your actual package name).
    * Locate the line:
        ```java
        String API_KEY = "YOUR_API_KEY_HERE";
        ```
    * **Replace `"YOUR_API_KEY_HERE"` with your actual TMDB API key** obtained from the prerequisites step.
    * Save the file.

5.  **Clean and Rebuild Project:**
    * In the Maven Tool Window, under your project name, expand "Lifecycle."
    * **Double-click `clean`**.
    * After `clean` finishes, go to the IntelliJ IDEA menu bar: **`Build` > `Rebuild Project`**.

## ▶️ How to Run

After completing the setup:

1.  **Using Maven (Recommended for JavaFX applications):**
    * In the **Maven Tool Window** (IntelliJ IDEA).
    * Under your project name, expand "Plugins."
    * Expand `javafx`.
    * **Double-click `javafx:run`**.

    This will launch the application window.

## 📂 Project Structure (Key Files)
![image](https://github.com/user-attachments/assets/f9caf335-aa23-4e5a-a575-275fe4837316)


This project is open-source and available under the [MIT License](LICENSE) 
