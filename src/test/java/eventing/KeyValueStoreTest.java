package eventing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class KeyValueStoreTest {

    final static UUID THE_UUID  = UUID.fromString("716dde0f-4736-46f6-bee4-e431fdd28d39");

    @Test
    @DisplayName("Should store key value pair")
    void shouldStoreKeyValuePair() {
        KeyValueStore.store(THE_UUID, List.of("716dde0f-4736-46f6-bee4-e431fdd28d39".split("")));

        var uuidCharacters = KeyValueStore.retrieve(THE_UUID).block();

        assertThat(uuidCharacters).startsWith("7", "1", "6", "d");
    }

    @Test
    @DisplayName("Should complete instead of emitting an empty list when entry does not exist")
    void shouldDoSomething() {
        UUID neverUsedId = UUID.randomUUID();

        StepVerifier.create(KeyValueStore.retrieve(neverUsedId))
                .verifyComplete();
    }
}