package udemy.movies.service.unreliable;

import reactor.core.publisher.Flux;
import udemy.movies.domain.MovieInfo;
import udemy.movies.service.MovieInfoService;

import java.time.LocalDate;
import java.util.List;

public class FlakyMovieInfoService {

    final static List<MovieInfo> fallbackMovieInfos = List.of(
            new MovieInfo(42L, "The Big Lebowski", List.of("John Goodman", "Jeff Bridges", "Steve Buscemi"), 1998, LocalDate.of(1998, 3, 6)),
            new MovieInfo(102, "Pulp Fiction", List.of("John Travolta", "Samuel L. Jackson", "Bruce Willis"), 1994, LocalDate.of(1994, 11, 03))
    );

    private final MovieInfoService movieInfoService;

    public FlakyMovieInfoService(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    public Flux<MovieInfo> movieInfos() {
        return movieInfoService.movieInfos()
                .take(1)
                .concatWith(Flux.error(new IllegalStateException("oopsie")));
    }

    public Flux<MovieInfo> movieInfosOnErrorReturn() {
        return movieInfos()
                .onErrorReturn(fallbackMovieInfos.getFirst());
    }
}
