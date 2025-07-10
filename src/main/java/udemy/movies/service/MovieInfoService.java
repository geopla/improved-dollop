package udemy.movies.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import udemy.movies.domain.MovieInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MovieInfoService {

    final static List<MovieInfo> movieInfos = List.of(
            new MovieInfo(100, "Batman Begins", List.of("Christian Bale", "Michael Cane"), 2005, LocalDate.parse("2005-06-15")),
            new MovieInfo(101, "The Dark Knight", List.of("Christian Bale", "HeathLedger"), 2008, LocalDate.parse("2008-07-18")),
            new MovieInfo(102, "Dark Knight Rises", List.of("Christian Bale", "Tom Hardy"), 2012, LocalDate.parse("2012-07-20"))
    );

    public Flux<MovieInfo> movieInfos() {
        return Flux.fromIterable(movieInfos);
    }

    public Mono<MovieInfo> movieInfo(long movieId) {
        return Mono.justOrEmpty(movieInfoById(movieId));
    }

    private Optional<MovieInfo> movieInfoById(long movieId) {
        return movieInfos.stream()
                .filter(movieInfo -> movieInfo.id() == movieId)
                .findFirst();
    }
}
