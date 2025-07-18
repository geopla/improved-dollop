package udemy.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Signal;
import reactor.test.StepVerifier;
import udemy.movies.domain.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

    @Test
    @DisplayName("Should do something with doOnEach()")
    void shouldDoSomethingWithDoOnEach() {
        var moviesWithSideEffect = movieService.allMovies()
                .doOnEach(signalConsumer);

        StepVerifier.create(moviesWithSideEffect)
                .expectNextCount(3)
                .verifyComplete();
    }

    Consumer<Signal<Movie>> signalConsumer = movieSignal -> {
        switch (movieSignal.getType()) {
            case SUBSCRIBE -> System.out.println("subscribe");
            case REQUEST -> System.out.println("request");
            case CANCEL -> System.out.println("cancel");
            case ON_SUBSCRIBE -> System.out.println("onSubscribe");
            case ON_NEXT -> System.out.println("onNext");
            case ON_ERROR -> System.out.println("onError");
            case ON_COMPLETE -> System.out.println("onComplete");
            case AFTER_TERMINATE -> System.out.println("afterTerminate");
            case CURRENT_CONTEXT -> System.out.println("currentContext");
            case ON_CONTEXT -> System.out.println("oneContext");
        }
    };
}