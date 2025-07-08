package udemy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple4;

import java.util.function.BiFunction;
import java.util.function.Function;

class ZipFluxAndMonoTest {

    ZipFluxAndMono service = new ZipFluxAndMono();

    @Test
    @DisplayName("Should zip strings from two publishers")
    void shouldZipStringsFromTwoPublisher() {
        var firstPublisher = Flux.just("A", "B", "C");
        var secondPublisher = Flux.just("d", "e", "f", "g");

        BiFunction<String, String, String> combinator = (first, second) -> first + second;

        StepVerifier.create(service.exploreZip(firstPublisher, secondPublisher, combinator))
                .expectNext("Ad", "Be", "Cf")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip string but implemented by zipWith()")
    void shouldZipStringButWithZipWith() {
        var firstFlux = Flux.just("A", "B", "C");
        var secondPublisher = Flux.just("d", "e", "f", "g");

        BiFunction<String, String, String> combinator = (first, second) -> first + second;

        StepVerifier.create(service.exploreZipWith(firstFlux, secondPublisher, combinator))
                .expectNext("Ad", "Be", "Cf")
                .verifyComplete();
    }

    @Test
    @DisplayName("Should zip more than two publisher (and concatenate the resulting tuple to a string)")
    void shouldZipMoreThanTwoPublishers() {
        var firstPublisher = Flux.just("A", "B", "C");
        var secondPublisher = Flux.just("D", "E", "F");
        var thirdPublisher = Flux.just(1, 2, 3);
        var fourthPublisher = Flux.just(4, 5, 6);

        Function<Tuple4<String, String, Integer, Integer>, String> tupleToString = tuple ->
                "%s-%s-%d-%d".formatted(tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4());

        StepVerifier.create(service.exploreZipMultiplePublishers(
                tupleToString,
                firstPublisher,
                secondPublisher,
                thirdPublisher,
                fourthPublisher
        ))
                .expectNext("A-D-1-4", "B-E-2-5", "C-F-3-6")
                .verifyComplete();
    }
}