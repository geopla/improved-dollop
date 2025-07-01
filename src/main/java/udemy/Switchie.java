package udemy;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

class Switchie {

    final static int MAX_SHORTNAME_LENGTH = 6;

    List<String> someRiverNames = List.of(
            "Ganges",
            "Mississippi",
            "Nil",
            "Sambesi",
            "Mekong"
    );

    Function<Flux<String>, Flux<String>> shortRiverNamesUppercased = riverNames ->
            riverNames
                    .filter(riverName -> riverName.length() <= MAX_SHORTNAME_LENGTH)
                    .map(String::toUpperCase);

    Function<Flux<String>, Flux<String>> longRiverNamesUppercased = riverNames ->
            riverNames
                    .filter(riverName -> riverName.length() > MAX_SHORTNAME_LENGTH)
                    .map(String::toUpperCase);


    Flux<String> rivers(Boolean shortNamesFlag) {
        Flux<String> someRiverNamesFlux = Flux.fromIterable(someRiverNames);

        return Mono.justOrEmpty(shortNamesFlag)
                .filter(snf -> snf)
                .flatMapMany(snf -> someRiverNamesFlux)
                .transform(shortRiverNamesUppercased)
                .switchIfEmpty(someRiverNamesFlux.transform(longRiverNamesUppercased));
    }

    Flux<String> monoPublisherInFluxSwitchIfEmpty(Flux<String> tasks) {
        return tasks.switchIfEmpty(Mono.just("drinking beer"));
    }
}
