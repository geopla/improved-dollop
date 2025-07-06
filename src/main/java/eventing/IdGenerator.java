package eventing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.UUID;
import java.util.function.Supplier;

class IdGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdGenerator.class);
    private Supplier<UUID> uuidSupplier;

    IdGenerator() {
        uuidSupplier = () -> UUID.randomUUID();
    }

    IdGenerator(Supplier<UUID> uuidSupplier) {
        this.uuidSupplier = uuidSupplier;
    }

    Mono<UUID> random() {
        LOGGER.info("requesting new id");
        // add a delay to mimic a real world HTTP request
        return Mono
                .delay(Duration.ofMillis(500))
                .map(delay -> {
                    LOGGER.info("uuid response received {}", delay);
                    return uuidSupplier.get();
                });
    }
}
