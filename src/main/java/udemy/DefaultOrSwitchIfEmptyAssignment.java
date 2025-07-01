package udemy;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

public class DefaultOrSwitchIfEmptyAssignment {

    static final String THE_NAME = "alex";

    static Function<Mono<String>, Mono<String>> filterMap(int greaterThen) {
        return name -> name.filter(s -> s.length() > greaterThen).map(String::toUpperCase);
    }

    static Function<Mono<String>, Mono<List<String>>> filterMapSplit(int greaterThen) {
        return name -> name
                .filter(s -> s.length() > greaterThen)
                .map(String::toUpperCase)
                .flatMap(DefaultOrSwitchIfEmptyAssignment::splitName);
    }

    static Mono<List<String>> splitName(String name) {
        return Mono.just(List.of(name.split("")));
    }

    Mono<String> nameMonoMapFilter(int greaterThen) {
        return Mono.just(THE_NAME)
                .transform(filterMap(greaterThen))
                .defaultIfEmpty("DEFAULT");
    }

    Mono<List<String>> nameMonoMapFilterSwitch(int greaterThen) {
        return Mono.just(THE_NAME)
                .transform(filterMapSplit(greaterThen))
                .switchIfEmpty(Mono.just("default")
                        .transform(filterMapSplit(0)));
    }
}
