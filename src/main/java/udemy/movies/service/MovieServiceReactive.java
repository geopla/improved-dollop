package udemy.movies.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.movies.domain.Movie;
import udemy.movies.domain.MovieInfo;
import udemy.movies.domain.Review;

import java.util.List;
import java.util.function.Function;

public class MovieServiceReactive {

    private final MovieInfoService movieInfoService;
    private final ReviewService reviewService;

    public MovieServiceReactive(MovieInfoService movieInfoService, ReviewService reviewService) {
        this.movieInfoService = movieInfoService;
        this.reviewService = reviewService;
    }

    public Flux<Movie> allMovies() {
        return movieInfoService.movieInfos()
                .flatMap(this::movieMapper);
    }

    private Mono<Movie> movieMapper(MovieInfo movieInfo) {
        return reviewService.allReviews(movieInfo.id())
                .map(toMovie(movieInfo));
    }

    private static Function<List<Review>, Movie> toMovie(MovieInfo movieInfo) {
        return reviewList -> new Movie(movieInfo, reviewList);
    }
}
