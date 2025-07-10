package eventing;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

class IdGeneratorTest {

    final static UUID THE_UUID = UUID.fromString("716dde0f-4736-46f6-bee4-e431fdd28d39");

    @Test
    @DisplayName("Should create a version 4 UUID")
    void shouldCreateId() {
        IdGenerator idGenerator= new IdGenerator();

        StepVerifier.create(idGenerator.random())
                .expectNextMatches(uuid -> uuid.version() == 4)
                .verifyComplete();
    }

    @Test
    @DisplayName("Should create a UUID with a specified by a Supplier")
    void shouldCreateIdWithSupplier() {
        IdGenerator idGenerator = new IdGenerator(() -> THE_UUID);

        StepVerifier.create(idGenerator.random())
                .expectNext(THE_UUID)
                .verifyComplete();
    }
}