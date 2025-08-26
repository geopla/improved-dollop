package udemy.movies.service;

import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import udemy.movies.domain.*;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MovieServiceReactive {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;
    private final RevenueService revenueService;

    public MovieServiceReactive(MovieInfoService movieInfoService, ReviewService reviewService, RevenueService revenueService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
        this.revenueService = revenueService;
    }

    public Flux<Movie> allMovies() {
        return movieInfoService.movieInfos()
                .flatMap(this::movieMapper)
                .onErrorMap(MovieServiceReactive::toMovieException)
                .retryWhen(fixedDelayThrowingOriginalException(2, Duration.ofMillis(100)));
    }

    private static Throwable toMovieException(Throwable t) {
        return new MovieException(t.getMessage());
    }

    private Retry fixedDelayThrowingOriginalException(int maxAttempts, Duration fixedDelay) {
        return Retry.fixedDelay(maxAttempts, fixedDelay)
                .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                        Exceptions.propagate(retrySignal.failure()));
    }

    public Mono<Movie> movieById(long id) {
        return movieInfoService.movieInfo(id)
                .flatMap(this::movieMapper);
    }

    Mono<Movie> movieMapper(MovieInfo movieInfo) {
        return reviewService.allReviews(movieInfo.id())
                .map(toMovie(movieInfo));
    }

    static Function<List<Review>, Movie> toMovie(MovieInfo movieInfo) {
        return reviewList -> new Movie(movieInfo, reviewList);
    }

    public Mono<Movie> movieByIdZipImplemented(long id) {
        return movieInfoService.movieInfo(id)
                .zipWith(reviewService.allReviews(id), movieCombinator);
    }

    final BiFunction<MovieInfo, List<Review>, Movie> movieCombinator = Movie::new;

    public Mono<Movie> movieByIdWithRevenue(long id) {
        return movieInfoService.movieInfo(id)
                .flatMap(movieInfo -> {
                    Mono<List<Review>> reviewsMono = reviewService.allReviews(id);
                    Mono<Revenue> revenueMono = revenueService.revenueMono(id);

                    return reviewsMono.zipWith(revenueMono, toMovieWithRevenue(movieInfo));
                });
    }

    static BiFunction<List<Review>, Revenue, Movie> toMovieWithRevenue(MovieInfo movieInfo) {
        return ((reviewList, revenue) -> new Movie(movieInfo, reviewList, revenue));
    }
}
