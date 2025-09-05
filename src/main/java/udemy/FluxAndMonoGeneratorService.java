package udemy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.List;
import java.util.function.BiFunction;

import static reactor.core.publisher.Flux.just;

public class FluxAndMonoGeneratorService {

    Mono<Tuple2<String, Integer>> exploreMonoZipWith() {
        var first = Mono.just("A");
        var second = Mono.just(1);

        return first.zipWith(second);
    }

    Mono<String> exploreMonoZipWithCombinator() {
        var firstMono = Mono.just("A");
        var secondMono = Mono.just(1);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstMono.zipWith(secondMono, combinator);
    }

    Flux<Tuple2<String, Integer>> exploreFluxZipWith() {
        var firstFlux = Flux.just("A", "B");
        var secondPublisher = Flux.just(1, 2);

        return firstFlux.zipWith(secondPublisher);
    }

    Flux<String> exploreFluxZipWithCombinator() {
        var firstFlux = Flux.just("A", "B");
        var secondPublisher = Flux.just(1, 2);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstFlux.zipWith(secondPublisher, combinator);
    }

    // zipWith() parameter doesn't need to be a Publisher at all

    Flux<Tuple2<String, Integer>> exploreFluxZipWithIterable() {
        var firstFlux = Flux.just("A", "B");
        var secondIterable = List.of(1, 2);

        return firstFlux.zipWithIterable(secondIterable);
    }

    Flux<String> exploreFluxZipWithIteratorCombinator() {
        var firstFlux = Flux.just("A", "B");
        var secondIterable = List.of(1, 2);

        BiFunction<String, Integer, String> combinator = "%s-%d"::formatted;

        return firstFlux.zipWithIterable(secondIterable, combinator);
    }


    Flux<String> exploreConcatWith(Publisher<String> monoOrFlux) {
        var aFlux = just("A");

        return aFlux.concatWith(monoOrFlux);
    }

    Flux<String> exploreConcatWithMono(Publisher<String> monoOrFlux) {
        var aMono = just("A");

        return aMono.concatWith(monoOrFlux);
    }

    Flux<String> exploreMerge() {
        var abcPublisher = Flux.just("A-100", "B-200", "C-300").delayElements(Duration.ofMillis(100));
        var defPublisher = Flux.just("d-125", "e-250", "f-375").delayElements(Duration.ofMillis(125));

        return Flux.merge(abcPublisher, defPublisher);
    }

    Flux<String> exploreMergeWith() {
        var abcPublisher = Flux.just("A-100", "B-200", "C-300").delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("d-125", "e-250", "f-375").delayElements(Duration.ofMillis(125));

        return defFlux.mergeWith(abcPublisher);
    }

    Flux<String> exploreMergeWithMono() {
        var aMono = Mono.just("A");
        var bPublisher = Mono.just("B");

        return aMono.mergeWith(bPublisher);
    }
}
