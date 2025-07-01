package udemy;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

public class DefaultOrSwitchIfEmpty {

    // --- Fluxin'

    List<String> alexBenChloe = List.of("alex", "ben", "chloe");

    static Function<Flux<String>, Flux<String>> filterMap(int stringLength) {
        return name -> name
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase);
    }

    static Function<Mono<String>, Mono<String>> filterMapMono(int stringLength) {
        return name -> name
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase);
    }

    static Function<Flux<String>, Flux<String>> filterMapSplit(int stringLength) {
        return name -> name
                .filter(s -> s.length() > stringLength)
                .map(String::toUpperCase)
                .flatMap(DefaultOrSwitchIfEmpty::splitString);
    }

    static Flux<String> splitString(String name) {
        return Flux.fromArray(name.split(""));
    }

    Flux<String> namesFluxTransformAndSplitOrDefault(int stringLength) {
        return Flux.fromIterable(alexBenChloe)
                .transform(filterMapSplit(stringLength))
                .defaultIfEmpty("default");
    }

    Flux<String> namesFluxTransformAndSplitOrApplyDefault(int stringLength) {
        var defaultWhenEmpty = "default";

        return Flux.fromIterable(alexBenChloe)
                .transform(filterMapSplit(stringLength))
                .switchIfEmpty(
                        Flux.just(defaultWhenEmpty)
                                .transform(filterMapSplit(0))
                );
    }

    Flux<String> namesFluxTransformWithFilterMapFunction(int stringLength) {
        return Flux.fromIterable(alexBenChloe)
                .transform(filterMap(stringLength));
    }

    // --- Flat map operator in Mono

    static Mono<List<String>> splitStringToList(String name) {
        return Mono.just(List.of(name.split("")));
    }

    Mono<String> namesMonoTransform(int stringLength) {
        return Mono.just("alex")
                .transform(filterMapMono(stringLength));
    }

    Mono<List<String>> namesMonoTransformAndSplit(int stringLength) {
        return Mono.just("alex")
                .transform(filterMapMono(stringLength))
                .flatMap(DefaultOrSwitchIfEmpty::splitStringToList);
    }

}
