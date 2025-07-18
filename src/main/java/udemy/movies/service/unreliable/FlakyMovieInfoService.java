package udemy.movies.service.unreliable;

import reactor.core.publisher.Flux;
import udemy.movies.domain.MovieInfo;
import udemy.movies.service.MovieInfoService;

public class FlakyMovieInfoService {

    private final MovieInfoService movieInfoService;

    public FlakyMovieInfoService(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }

    public Flux<MovieInfo> movieInfos() {
        return movieInfoService.movieInfos()
                .take(1)
                .concatWith(Flux.error(new IllegalStateException("oopsie")));
    }
}
