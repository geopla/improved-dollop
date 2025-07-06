package eventing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class IdSplitterService {

    record SplitterResult(
            boolean success,
            String message
    ) {
    }

    private Sinks.Many<SplitterResult> splitterResult = Sinks.many().unicast().onBackpressureBuffer();

    private final IdGenerator idGenerator;

    IdSplitterService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    Flux<SplitterResult> splitterResult() {
        return splitterResult.asFlux();
    }

    Mono<Void> processNextId() {
        return idGenerator.random()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(uuid ->
                        split(uuid)
                                .flatMap(this::store)
                );
    }

    private Mono<List<String>> split(UUID uuid) {
        // introduce delay because processing the data is a long-running task
        return Mono
                .delay(Duration.ofMillis(500))
                .map(delay -> Arrays.stream(uuid.toString().split("")).toList());
    }

    private Mono<Void> store(List<String> uuidCharacters) {
        // TODO add a delay because insert to the database takes time too?
        return Mono.empty();
    }
}
