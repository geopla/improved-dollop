package udemy.movies.service.unreliable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.service.MovieInfoService;

class FlakyMovieInfoServiceTest {

    FlakyMovieInfoService flakyMovieInfoService = new FlakyMovieInfoService(
            new MovieInfoService()
    );

    @Test
    @DisplayName("Should fail after emiting first movie info")
    void shouldFailAfterEmittingFirstMovieInfo() {
        StepVerifier.create(flakyMovieInfoService.movieInfos())
                .expectNextMatches(movieInfo -> movieInfo.name().equals("Batman Begins"))
                .expectErrorMatches(e -> e.getMessage().contains("oopsie"))
                .verify();
    }
}