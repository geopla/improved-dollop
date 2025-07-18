package udemy.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.domain.Movie;

import java.util.ArrayList;
import java.util.List;

class MovieServiceReactiveTest {

    MovieServiceReactive movieService;

    @BeforeEach
    void setUp() {
        movieService = new MovieServiceReactive(
                new MovieInfoService(),
                new ReviewService()
        );
    }

    @Test
    @DisplayName("Should deliver all available movies")
    void shouldDeliverAllMovies() {
        List<Movie> moviesRecorded = new ArrayList<>();

        StepVerifier.create(movieService.allMovies())
                .recordWith(() -> moviesRecorded)
                .expectNextCount(3)
                .expectRecordedMatches(movies -> {
                    var movieNames = movies.stream()
                            .map(movie -> movie.info().name())
                            .toList();

                    return movieNames.containsAll(List.of(
                            "Batman Begins",
                            "The Dark Knight",
                            "Dark Knight Rises"
                    ));
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Should deliver a movie by id")
    void shouldDeliverMovieById() {
        var movieIdTheDarkKnight = 101;

        StepVerifier.create(movieService.movieById(movieIdTheDarkKnight))
                .expectNextMatches(movie -> movie.info().name().equals("The Dark Knight"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Should deliver a movie by id implemented by zipping")
    void shouldDeliverMovieByIdZipImplemented() {
        var movieIdTheDarkKnight = 101;

        StepVerifier.create(movieService.movieByIdZipImplemented(movieIdTheDarkKnight))
                .expectNextMatches(movie -> movie.info().name().equals("The Dark Knight"))
                .verifyComplete();
    }
}