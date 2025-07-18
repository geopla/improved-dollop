package udemy.movies.service.unreliable;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import udemy.movies.domain.MovieInfo;
import udemy.movies.service.MovieInfoService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

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

    @Test
    @DisplayName("Should use a fallback movie info in case of an error")
    void shouldUseFallbackMovieInfo() {
        List<MovieInfo> movieInfos = new ArrayList<>();

        StepVerifier.create(flakyMovieInfoService.movieInfosOnErrorReturn())
                .recordWith(() -> movieInfos)
                .expectNextCount(2)
                .expectRecordedMatches(containsAllMovieNames("Batman Begins","The Big Lebowski"))
                .verifyComplete();
    }

    private static Predicate<Collection<MovieInfo>> containsAllMovieNames(String ... names) {
        return movieInfo -> movieInfo.stream()
                .map(MovieInfo::name)
                .toList()
                .containsAll(List.of(names));
    }
}