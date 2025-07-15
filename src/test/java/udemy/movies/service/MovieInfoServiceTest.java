package udemy.movies.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.domain.MovieInfo;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MovieInfoServiceTest {

    MovieInfoService movieInfoService = new MovieInfoService();

    @Test
    @DisplayName("Should deliver all available movie infos")
    void shouldDeliverAllMovieInfosFromDatabase() {
        List<MovieInfo> movieInfos = new ArrayList<>();

        StepVerifier.create(movieInfoService.movieInfos())
                .recordWith(() -> movieInfos)
                .expectNextCount(3)
                .verifyComplete();

        assertThat(movieInfos).containsExactlyInAnyOrderElementsOf(MovieInfoService.movieInfos);
    }

    @Test
    @DisplayName("Should deliver an existing movie info")
    void shouldDeliverExistingMovieInfo() {
        var movieIdDarkKnightRises = 102;

        StepVerifier.create(movieInfoService.movieInfo(movieIdDarkKnightRises))
                .expectNext(MovieInfoService.movieInfos.getLast())
                .verifyComplete();
    }

    @Test
    @DisplayName("Should deliver an empty Mono when movie info does NOT exist")
    void shouldDeliverEmptyMono() {
        var movieIdWhichDoesNotExist = 452;

        StepVerifier.create(movieInfoService.movieInfo(movieIdWhichDoesNotExist))
                .verifyComplete();
    }
}