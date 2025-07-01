package udemy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

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
}
