package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

class MergeFluxAndMonoTest {


    @Test
    @DisplayName("Should merge Mono with Publisher interleaved")
    void shouldMergeMonoWithFluxInterleaved() {
        var lazyMono = Mono.just("A").delayElement(Duration.ofMillis(100));
        var eagerPublisher = Flux.just("B", "C");

        StepVerifier.create(lazyMono.mergeWith(eagerPublisher))
                .expectNext("B", "C", "A")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should merge Publishers interleaved")
    void shouldMergePublishersInterleaved() {
        var lazyPublisher = Mono.just("A").delayElement(Duration.ofMillis(100));
        var eagerPublisher = Flux.just("B", "C");

        StepVerifier.create(Flux.merge(lazyPublisher, eagerPublisher))
                .expectNext("B", "C", "A")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should merge Flux with Publisher interleaved")
    void shouldMergeFluxWithPublisher() {
        var lazyFlux = Flux.just("A", "B").delayElements(Duration.ofMillis(100));
        var eagerPublisher = Mono.just("C");

        StepVerifier.create(lazyFlux.mergeWith(eagerPublisher))
                .expectNext("C", "A", "B")
                .verifyComplete();
    }
}