package eventing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SplitterServiceTest {

    final static String THE_UUID_STRING = "716dde0f-4736-46f6-bee4-e431fdd28d39";
    final static UUID THE_UUID  = UUID.fromString(THE_UUID_STRING);

    SplitterService splitterService;

    @BeforeEach
    void setUp() {
        splitterService = new SplitterService(new IdGenerator(() -> THE_UUID));
    }

    @Test
    @DisplayName("Should store UUID characters")
    void shouldStoreCharacters() {
        splitterService.processNextId().block();

        var uuidChars = KeyValueStore.retrieve(THE_UUID).block();

        assertThat(uuidChars).containsExactly(THE_UUID_STRING.split(""));
    }

    @Test
    @DisplayName("Should emit result")
    void shouldEmitResult() {
        StepVerifier.create(splitterService.splitterResult())
                .then(() -> splitterService.processNextId().subscribe())
                .expectNextMatches(p -> p.uuid().equals(THE_UUID))
                .thenCancel()
                .verify();
    }
}