package eventing;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

class SplitterService {

    record SplitterResult(
            boolean success,
            UUID uuid,
            String message
    ) {
    }

    private Sinks.Many<SplitterResult> splitterResult = Sinks.many().unicast().onBackpressureBuffer();

    private final IdGenerator idGenerator;

    SplitterService(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    Flux<SplitterResult> splitterResult() {
        return splitterResult.asFlux();
    }

    Mono<UUID> processNextId() {
        return idGenerator.random()
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::splitStoreNotify);
    }

    private Mono<UUID> splitStoreNotify(UUID uuid) {
        return split(uuid)
                .flatMap(chars -> store(uuid, chars))
                .doOnSuccess(storedUuid ->
                        splitterResult.tryEmitNext(new SplitterResult(true, uuid, "stored successfully"))
                );
    }

    private Mono<List<String>> split(UUID uuid) {
        // introduce delay because processing the data is a long-running task
        return Mono
                .delay(Duration.ofMillis(500))
                .map(delay -> Arrays.stream(uuid.toString().split("")).toList());
    }

    private Mono<UUID> store(UUID uuid, List<String> uuidCharacters) {
        // no delay, just do it like a reactive database
        return KeyValueStore.store(uuid, uuidCharacters);
    }
}
