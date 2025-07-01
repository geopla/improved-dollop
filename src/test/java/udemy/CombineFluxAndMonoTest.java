package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

class CombineFluxAndMonoTest {


    // --- Investigate some Mono and Flux concat methods

    @Test
    @DisplayName("Should concat Mono with a SINGLE flux")
    void shouldConcatMonoWithFlux() {
        Flux<String> concatResult = Mono.just("A")
                .concatWith(Flux.just("b", "c"));

        StepVerifier.create(concatResult)
                .expectNext("A", "b", "c")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should concat Flux with SINGLE Flux")
    void shouldConcatFluxWithSingleFluxies() {
        Flux<String> concatResult = Flux.just("A", "B")
                .concatWith(Flux.just("c", "d"));

        StepVerifier.create(concatResult)
                .expectNext("A", "B", "c", "d")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should concat multiple Publisher")
    void shouldConcatMultiplePublishers() {
        Flux<String> concatResult = Flux.concat(
                Flux.just("A", "B"),
                Flux.just("c", "d"),
                Mono.just("e")
        );

        StepVerifier.create(concatResult)
                .expectNext("A", "B", "c", "d", "e")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should concat Flux with some values")
    void shouldConcatFluxWithSomeValues() {
        Flux<String> concatResult = Flux.just("A", "B")
                .concatWithValues("c", "d");

        StepVerifier.create(concatResult)
                .expectNext("A", "B", "c", "d")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should gather publisher errors into a CompositeException when concat with delaying errors")
    void shouldGatherPublisherErrors() {
        var abOoopieErrorFlux = Flux.just("A", "B").concatWith(Flux.error(new IllegalStateException("Oopsie")));
        var cdFlux = Flux.just("C", "D");
        var efUmphErrorFlux = Flux.just("E", "F").concatWith(Flux.error(new IllegalStateException("Umph")));

        var concatResult = Flux.concatDelayError(
                abOoopieErrorFlux,
                cdFlux,
                efUmphErrorFlux
        );

        StepVerifier.create(concatResult)
                .expectNext("A", "B", "C", "D", "E", "F")
                .expectErrorMatches(throwable -> {
                    // shite, Exceptions.CompositeExceptions is package private - can only validate the suppressed ones
                    var suppressed = throwable.getSuppressed();
                    var errorMessages = Arrays.stream(suppressed)
                            .map(Throwable::getMessage)
                            .toList();
                    return errorMessages.containsAll(List.of("Oopsie", "Umph"));
                })
                .verify();
    }
}