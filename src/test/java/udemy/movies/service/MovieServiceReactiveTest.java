package udemy.movies.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Signal;
import reactor.test.StepVerifier;
import udemy.movies.domain.Movie;
import udemy.movies.domain.MovieException;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovieServiceReactiveTest {

    MovieServiceReactive movieService;

    @Mock
    ReviewService reviewServiceMock;

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
    @DisplayName("Should throw business exception when movie service fails while fetching all movies")
    void shouldThrowMovieExceptionWhenReviewServiceFails() {
        when(reviewServiceMock.allReviews(anyLong())).thenThrow(new NoSuchElementException("oopsie"));

        var service = new MovieServiceReactive(
                new MovieInfoService(),
                reviewServiceMock
        );

        StepVerifier.create(service.allMovies())
                .verifyError(MovieException.class);
    }

    @Test
    @DisplayName("Should retry 2 times when review service fails")
    void shouldRetryWhenReviewServiceFails() {
        when(reviewServiceMock.allReviews(anyLong())).thenThrow(new NoSuchElementException("oopsie"));

        var service = new MovieServiceReactive(
                new MovieInfoService(),
                reviewServiceMock
        );

        StepVerifier.create(service.allMovies())
                .verifyError(Throwable.class);

        verify(reviewServiceMock, times(3)).allReviews(isA(Long.class));
    }

    @Test
    @DisplayName("Should throw original exception after exhausted retries")
    void shouldThrowOriginalExceptionAfterExhaustedRetries() {
        when(reviewServiceMock.allReviews(anyLong())).thenThrow(new NoSuchElementException("oopsie"));

        var service = new MovieServiceReactive(
                new MovieInfoService(),
                reviewServiceMock
        );

        StepVerifier.create(service.allMovies())
                .verifyError(MovieException.class);
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