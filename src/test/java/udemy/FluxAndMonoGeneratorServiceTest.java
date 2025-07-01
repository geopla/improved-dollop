package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;


class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService service =  new FluxAndMonoGeneratorService();

    @ParameterizedTest
    @MethodSource("concatWithPublisher")
    @DisplayName("Should concat Flux with either Mono or Flux")
    void shouldConcatFluxWithPublisher(Publisher<String> monoOrFlux, List<String> expectedResult) {
        StepVerifier.create(service.exploreConcatWith(monoOrFlux))
                .expectNextSequence(expectedResult)
                .verifyComplete();
    }

    @ParameterizedTest
    @MethodSource("concatWithPublisher")
    @DisplayName("Should concat Mono with either Mono or Flux")
    void shouldConcatMonoWithPublisher(Publisher<String> monoOrFlux, List<String> expectedResult) {
        StepVerifier.create(service.exploreConcatWithMono(monoOrFlux))
                .expectNextSequence(expectedResult)
                .verifyComplete();
    }

    public static Stream<Arguments> concatWithPublisher() {
        return Stream.of(
                arguments(Mono.just("B"), List.of("A", "B")),
                arguments(Flux.just("B", "C"), List.of("A", "B", "C"))
        );
    }
}