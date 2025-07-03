package udemy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static reactor.core.publisher.Flux.just;

public class FluxAndMonoGeneratorService {

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
