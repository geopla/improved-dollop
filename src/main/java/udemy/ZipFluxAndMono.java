package udemy;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple4;

import java.util.function.BiFunction;
import java.util.function.Function;

public class ZipFluxAndMono {

    public Flux<String> exploreZip(
            Publisher<String> first,
            Publisher<String> second,
            BiFunction<String, String, String> combinator) {

        return Flux.zip(first, second, combinator);
    }

    // note the slightly different signature from the method above
    public Flux<String> exploreZipWith(
            Flux<String> first,
            Publisher<String> second,
            BiFunction<String, String, String> combinator) {

        return first.zipWith(second, combinator);
    }

    // note the different type parameters
    public Flux<String> exploreZipMultiplePublishers(
            Function<Tuple4<String, String, Integer, Integer>, String> tupleToString,
            Publisher<String> firstStringPublisher,
            Publisher<String> secondStringPublisher,
            Publisher<Integer> firstIntegerPublisher,
            Publisher<Integer> secondIntegerPublisher) {

        return Flux.zip(firstStringPublisher, secondStringPublisher, firstIntegerPublisher, secondIntegerPublisher)
                .map(tupleToString);
    }
}
