package udemy.movies.service;

import reactor.core.publisher.Mono;
import udemy.movies.domain.Revenue;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public class RevenueService {

    public Revenue revenue(Long movieId) {

        // note, no reactive return type here
        // we are a long-running blocking call

        delay(1000, ChronoUnit.MILLIS);

        return new Revenue(
                movieId,
                1_000_000,
                5_000_000
        );
    }

    public Mono<Revenue> revenueMono(Long movieId) {
        return Mono.just(
                new Revenue(
                        movieId,
                        1_000_000,
                        5_000_000
                )
        );
    }

    void delay(long amount, ChronoUnit unit) {
        try {
            Thread.sleep(Duration.of(amount, unit));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
